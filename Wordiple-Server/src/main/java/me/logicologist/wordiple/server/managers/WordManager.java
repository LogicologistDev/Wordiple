package me.logicologist.wordiple.server.managers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
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
        this.validWords = new ArrayList<>();
        this.guessableWords = new ArrayList<>();
        try {
            File validWordsFile = new File("data", "validwords.txt");
            File guessableWordsFile = new File("data", "guessablewords.txt");

            if (!validWordsFile.exists()) {
                validWordsFile.mkdirs();
                validWordsFile.createNewFile();
            }
            if (!guessableWordsFile.exists()) {
                guessableWordsFile.mkdirs();
                guessableWordsFile.createNewFile();
            }

            Files.copy(getClass().getResourceAsStream("/validwords.txt"), validWordsFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            BufferedReader br = new BufferedReader(new FileReader(validWordsFile));
            String st;
            while ((st = br.readLine()) != null) this.validWords.add(st);

            Files.copy(getClass().getResourceAsStream("/guessablewords.txt"), guessableWordsFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            br = new BufferedReader(new FileReader(guessableWordsFile));
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
