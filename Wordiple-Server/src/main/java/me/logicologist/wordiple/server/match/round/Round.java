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

    private final String word;
    private final HashMap<WordipleUser, List<String>> guesses;
    private final long startTime;
    private ScheduledFuture<?> roundTimer;
    private int maxGuesses = 6;
    private WordipleUser winner;


    public Round() {
        this.word = WordManager.getInstance().getRandomGuessableWord().toUpperCase();
        System.out.println("The word is: " + word);
        startTime = System.currentTimeMillis();
        this.guesses = new HashMap<>();
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

        System.out.println("Calculating timer...");

        int leastGuesses = 6;
        for (List<String> guess : guesses.values()) {
            leastGuesses = Math.min(leastGuesses, guess.size());
        }
        int possibleTimer = (guessNumber - leastGuesses) * 20 + 5;
        long timerEnd = System.currentTimeMillis() + possibleTimer * 1000L;

        System.out.println("Checking if correct");

        if (possibleTimer > 0 && text.equals(word)) {
            System.out.println("Correct");
            maxGuesses = guessNumber;
            winner = (winner == null ? guesser : null);
            guesser.addGuess(guessNumber);
            guesser.addSolveTime(Math.round(System.currentTimeMillis() - startTime / 10.0) / 100.0);
            System.out.println(timerEnd - System.currentTimeMillis() + " milliseconds left");
            roundTimer = WordipleServer.getExecutor().schedule(() -> {
                endRound(guesser);
            }, timerEnd - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
            if (winner == null) {
                System.out.println("Ending from double correct");
                endRound(guesser);
                roundTimer.cancel(true);
                return;
            }
        }

        System.out.println("Are guesses left?");

        if (!guessesLeft() && winner != null) {
            System.out.println("Nope. Ending round");
            endRound(guesser);
            if (roundTimer != null) roundTimer.cancel(true);
            return;
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

        System.out.println("Is word valid?");

        if (!WordManager.getInstance().isValid(text)) {
            System.out.println("No. Theyre cheating");
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

            if (text.equals(word) && roundTimer == null) {
                if (possibleTimer > 0) {
                    PacketManager.getInstance().getSocket().getPacket(SolvePacket.class).sendPacket(packet -> packet.getPacketType().getArguments()
                                    .setValues("player", guesser.getUsername())
                                    .setValues("timerend", timerEnd)
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
        WordipleServer.getExecutor().schedule(match::startRound, 8, TimeUnit.SECONDS);
    }

    private boolean guessesLeft() {
        for (List<String> guess : guesses.values()) {
            if (guess.size() < maxGuesses) return true;
        }
        return false;
    }
}
