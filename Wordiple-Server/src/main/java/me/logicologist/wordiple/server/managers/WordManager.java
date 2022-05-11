package me.logicologist.wordiple.server.managers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class WordManager {

    private static WordManager instance;
    private final List<String> validWords;
    private final List<String> guessableWords;

    public WordManager() {
        instance = null
        this.validWords = new ArrayList<>();
        this.guessableWords = new ArrayList<>();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/validwords.txt")));
            String st;
            while ((st = br.readLine()) != null) this.validWords.add(st);

            br = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/guessablewords.txt")));
            while ((st = br.readLine()) != null) this.guessableWords.add(st.toLowerCase());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public boolean isValid(String word) {
        if (word.length() != 5) return false;
        return validWords.contains(word);
    }

    public String getRandomGuessableWord() {
        return guessableWords.get(new Random().nextInt(guessableWords.size()));
    }

    public static WordManager getInstance() {
        return instance;
    }
}
