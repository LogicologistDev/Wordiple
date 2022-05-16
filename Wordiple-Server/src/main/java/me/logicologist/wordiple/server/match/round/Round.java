package me.logicologist.wordiple.server.match.round;

import me.logicologist.wordiple.server.WordipleServer;
import me.logicologist.wordiple.server.managers.MatchManager;
import me.logicologist.wordiple.server.managers.PacketManager;
import me.logicologist.wordiple.server.managers.SessionManager;
import me.logicologist.wordiple.server.managers.WordManager;
import me.logicologist.wordiple.server.match.Match;
import me.logicologist.wordiple.server.packets.game.*;
import me.logicologist.wordiple.server.user.WordipleUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public abstract class Round {


    //    private final List<> replay; need to add some replay packet or something for replays

    public final HashMap<WordipleUser, Double> solveTimes;
    public final HashMap<WordipleUser, List<String>> guesses;
    private final String word;
    private final long startTime;
    private ScheduledFuture<?> roundTimer;
    private int maxGuesses = 6;
    private WordipleUser winner;


    public Round() {
        this.word = WordManager.getInstance().getRandomGuessableWord().toUpperCase();
        System.out.println("The word is: " + word);
        startTime = System.currentTimeMillis();
        this.guesses = new HashMap<>();
        this.solveTimes = new HashMap<>();
    }

    public void setDisplayText(String username, String text) {
        int guess = guesses.get(SessionManager.getInstance().getSessionFromUsername(username)).size() + 1;
        for (WordipleUser user : guesses.keySet()) {
            if (user.getUsername().equals(username)) continue;
            PacketManager.getInstance().getSocket().getPacket(UpdateDisplayPacket.class).sendPacket(packet -> packet.getPacketType().getArguments()
                            .setValues("name", username)
                            .setValues("length", text.length())
                            .setValues("guess", guess),
                    user.getOutputStream()
            );
        }
    }

    public void addPlayer(WordipleUser user) {
        guesses.put(user, new ArrayList<>());
    }

    public void addGuess(WordipleUser guesser, String text) {

        guesses.get(guesser).add(text);

        int guessNumber = guesses.get(guesser).size();

        if (guessNumber == 1) {
            guesser.addOpener(text);
        }

        List<Character> wordLetters = new ArrayList<>();
        for (int i = 0; i < word.length(); i++) {
            wordLetters.add(word.charAt(i));
        }

        StringBuilder code = new StringBuilder();

        int leastGuesses = 6;
        for (List<String> guess : guesses.values()) {
            leastGuesses = Math.min(leastGuesses, guess.size());
        }
        int possibleTimer = (guessNumber - leastGuesses) * 20;
        if (possibleTimer > 0) possibleTimer += 5;
        long timerEnd = System.currentTimeMillis() + possibleTimer * 1000L;

        if (text.equals(word)) {
            maxGuesses = guessNumber;
            winner = (winner == null ? guesser : null);
            guesser.addGuess(guessNumber);
            double solveTime = Math.round(System.currentTimeMillis() - startTime / 10.0) / 100.0;
            guesser.addSolveTime(solveTime);
            solveTimes.put(guesser, solveTime);
            if (winner != null && possibleTimer > 0 && roundTimer == null) roundTimer = WordipleServer.getExecutor().schedule(() -> {
                endRound(guesser);
            }, timerEnd - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
        }

        if (!text.equals(word) && guessesLeft() && guessNumber >= 6) {
            timerEnd = System.currentTimeMillis() + (guessNumber - leastGuesses) * 120 * 1000L;
            long finalTimerEnd = timerEnd;
            for (WordipleUser user : guesses.keySet()) {
                PacketManager.getInstance().getSocket().getPacket(StartTimerPacket.class).sendPacket(packet -> packet.getPacketType().getArguments()
                            .setValues("player", guesser.getUsername())
                            .setValues("timerend", finalTimerEnd)
                            .setValues("guesslimit", 6),
                    user.getOutputStream()
                );
            }
            roundTimer = WordipleServer.getExecutor().schedule(() -> {
                endRound(guesser);
            }, timerEnd - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
        }

        if (!guessesLeft()) {
            endRound(guesser);
            if (roundTimer != null) {
                roundTimer.cancel(true);
                roundTimer = null;
            }
        }

        for (int i = 0; i < text.length(); i++) {
            if (wordLetters.get(i).equals(text.charAt(i))) {
                code.append("c");
                wordLetters.set(i, ' ');
                continue;
            }
            code.append("r");
        }

        for (int i = 0; i < text.length(); i++) {
            if (code.charAt(i) != 'r') continue;
            if (wordLetters.contains(text.charAt(i))) {
                code.setCharAt(i, 'i');
                for (int i2 = 0; i2 < wordLetters.size(); i2++) {
                    if (wordLetters.get(i2).equals(text.charAt(i))) {
                        wordLetters.set(i2, ' ');
                        break;
                    }
                }
            }
        }

        if (!WordManager.getInstance().isValid(text)) {
            guesses.get(guesser).add(text);
            code.setCharAt(0, 'l');
            code.setCharAt(1, 'l');
            code.setCharAt(2, 'l');
            code.setCharAt(3, 'l');
            code.setCharAt(4, 'l');
            return;
        }

        for (WordipleUser user : guesses.keySet()) {

            PacketManager.getInstance().getSocket().getPacket(GuessResponsePacket.class).sendPacket(packet -> packet.getPacketType().getArguments()
                            .setValues("player", guesser.getUsername())
                            .setValues("guess", guessNumber)
                            .setValues("data", code.toString()),
                    user.getOutputStream()
            );

            if (text.equals(word) && (roundTimer != null || !guessesLeft())) {
                if (possibleTimer > 0) {
                    long finalTimerEnd = timerEnd;
                    PacketManager.getInstance().getSocket().getPacket(StartTimerPacket.class).sendPacket(packet -> packet.getPacketType().getArguments()
                                    .setValues("player", guesser.getUsername())
                                    .setValues("timerend", finalTimerEnd)
                                    .setValues("guesslimit", guessNumber),
                            user.getOutputStream()
                    );
                }
                for (int i = guessNumber + 1; i <= 6; i++) {
                    int finalI = i;
                    for (WordipleUser user2 : guesses.keySet()) {
                        PacketManager.getInstance().getSocket().getPacket(GuessResponsePacket.class).sendPacket(packet -> packet.getPacketType().getArguments()
                                        .setValues("player", user2.getUsername())
                                        .setValues("guess", finalI)
                                        .setValues("data", "lllll"),
                                user.getOutputStream()
                        );
                    }
                }
                continue;
            }

            if (guessNumber >= maxGuesses || user == guesser) continue;

            PacketManager.getInstance().getSocket().getPacket(GuessResponsePacket.class).sendPacket(packet -> packet.getPacketType().getArguments()
                            .setValues("player", guesser.getUsername())
                            .setValues("guess", guessNumber + 1)
                            .setValues("data", "rrrrr"),
                    user.getOutputStream()
            );
        }
    }

    public void endRound(WordipleUser reference) {
        Match match = MatchManager.getInstance().getMatch(reference);
        for (WordipleUser user : guesses.keySet()) {
            solveTimes.putIfAbsent(user, -1.0);
        }
        match.setRoundWinner(winner);

        WordipleServer.getExecutor().schedule(() -> {
            for (WordipleUser user : guesses.keySet()) {
                PacketManager.getInstance().getSocket().getPacket(GameOverlayPacket.class).sendPacket(packet -> packet.getPacketType().getArguments().setValues("display", "The word was..."),
                        user.getOutputStream()
                );
            }
        }, 1, TimeUnit.SECONDS);
        WordipleServer.getExecutor().schedule(() -> {
            for (WordipleUser user : guesses.keySet()) {
                PacketManager.getInstance().getSocket().getPacket(GameOverlayPacket.class).sendPacket(packet -> packet.getPacketType().getArguments().setValues("display", this.word + "!"),
                        user.getOutputStream()
                );
            }
        }, 2, TimeUnit.SECONDS);
        WordipleServer.getExecutor().schedule(() -> {
            for (WordipleUser user : guesses.keySet()) {
                PacketManager.getInstance().getSocket().getPacket(ResetBoardsPacket.class).sendPacket(packet -> packet.getPacketType().getArguments(),
                        user.getOutputStream()
                );
            }
        }, 5, TimeUnit.SECONDS);
        if (winner == null || ((List<?>) match.score.get(winner)).size() < match.winGoal) WordipleServer.getExecutor().schedule(match::startRound, 8, TimeUnit.SECONDS);
    }

    private boolean guessesLeft() {
        for (List<String> guess : guesses.values()) {
            if (guess.size() < maxGuesses) return true;
        }
        return false;
    }
}
