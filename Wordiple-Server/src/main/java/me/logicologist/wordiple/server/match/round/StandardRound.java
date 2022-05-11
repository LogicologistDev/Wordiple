package me.logicologist.wordiple.server.match.round;

import me.logicologist.wordiple.server.managers.WordManager;
import me.logicologist.wordiple.server.user.WordipleUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class StandardRound {

    private final String word;
    private final HashMap<WordipleUser, List<String>> guesses;
//    private final List<> replay; need to add some replay packet or something for replays

    public StandardRound(WordipleUser playerOne, WordipleUser playerTwo) {
        this.word = WordManager.getInstance().getRandomGuessableWord();
        this.guesses = new HashMap<>();

        guesses.put(playerOne, new ArrayList<>());
        guesses.put(playerTwo, new ArrayList<>());
    }
}
