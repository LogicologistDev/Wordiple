package me.logicologist.wordiple.server.match.round;

import me.logicologist.wordiple.server.managers.PacketManager;
import me.logicologist.wordiple.server.managers.SessionManager;
import me.logicologist.wordiple.server.managers.WordManager;
import me.logicologist.wordiple.server.packets.game.GuessResponsePacket;
import me.logicologist.wordiple.server.packets.game.UpdateDisplayPacket;
import me.logicologist.wordiple.server.user.WordipleUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public abstract class Round {

    private final String word;
    private final HashMap<WordipleUser, List<String>> guesses;

    public Round() {
        this.word = WordManager.getInstance().getRandomGuessableWord().toUpperCase();
        System.out.println("The word is: " + word);
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

        List<Character> wordLetters = new ArrayList<>();
        for (int i = 0; i < word.length(); i++) {
            wordLetters.add(word.charAt(i));
        }

        StringBuilder code = new StringBuilder();

        for (int i = 0; i < text.length(); i++) {
            if (wordLetters.get(i).equals(text.charAt(i))) {
                code.append("c");
                wordLetters.set(i, ' ');
                continue;
            }
            if (wordLetters.contains(text.charAt(i))) {
                code.append("i");
                for (int i2 = 0; i2 < wordLetters.size(); i2++) {
                    if (wordLetters.get(i2).equals(text.charAt(i))) {
                        wordLetters.set(i2, ' ');
                        break;
                    }
                }
                continue;
            }
            code.append('r');
        }

        for (WordipleUser user : guesses.keySet()) {
            PacketManager.getInstance().getSocket().getPacket(GuessResponsePacket.class).sendPacket(packet -> packet.getPacketType().getArguments()
                            .setValues("player", guesser.getUsername())
                            .setValues("guess", guessNumber)
                            .setValues("data", code.toString()),
                    user.getOutputStream()
            );
            if (guessNumber >= 6 || user == guesser) continue;
            PacketManager.getInstance().getSocket().getPacket(GuessResponsePacket.class).sendPacket(packet -> packet.getPacketType().getArguments()
                            .setValues("player", guesser.getUsername())
                            .setValues("guess", guessNumber + 1)
                            .setValues("data", "rrrrr"),
                    user.getOutputStream()
            );
        }
    }

}
