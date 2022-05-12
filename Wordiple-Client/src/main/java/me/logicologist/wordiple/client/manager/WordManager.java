package me.logicologist.wordiple.client.manager;

import me.logicologist.wordiple.client.WordipleClient;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class WordManager {

    private final List<String> validWords;
    private static WordManager instance;

    public WordManager() {
        instance = this;
        this.validWords = new ArrayList<>();
        WordipleClient.getExecutor().submit(() -> {
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/validwords.txt")));
                String st;
                while ((st = br.readLine()) != null) this.validWords.add(st);
                System.out.println("Loaded " + validWords.size() + " valid words.");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }

    public boolean isValid(String word) {
        if (word.length() != 5) return false;
        return validWords.contains(word.toLowerCase());
    }

    public static WordManager getInstance() {
        return instance;
    }
}
