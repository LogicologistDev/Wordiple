package me.logicologist.wordiple.updater;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class WordipleUpdater {

    public static void main(String[] args) {
        List<String> argsList = Arrays.asList(args);
        File jarFile = new File(argsList.contains("-module") ? argsList.get(argsList.indexOf("-module") + 1) : "");
        if (!jarFile.exists()) return;

        
    }
}
