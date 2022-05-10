package me.logicologist.wordiple.client.manager;

import me.logicologist.wordiple.client.WordipleClient;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

public class WordManager {

    private final List<String> validWords;
    private static WordManager instance;

    public WordManager() {
        this.validWords = new ArrayList<>();
        try {
            File words = new File(WordipleClient.getAppData(), "validwords.txt");

            if (!words.exists()) words.createNewFile();

            Files.copy(getClass().getResourceAsStream("/validwords.txt"), words.toPath(), StandardCopyOption.REPLACE_EXISTING);
            BufferedReader br = new BufferedReader(new FileReader(words));
            String st;
            while ((st = br.readLine()) != null) this.validWords.add(st);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public boolean isValid(String word) {
        if (word.length() != 5) return false;
        return validWords.contains(word);
    }

    public static WordManager getInstance() {
        return instance;
    }
}
