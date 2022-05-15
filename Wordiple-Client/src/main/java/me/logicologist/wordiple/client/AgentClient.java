package me.logicologist.wordiple.client;

import java.io.*;
import java.util.Arrays;
import java.util.List;

public class AgentClient {

    private static FileWriter logWriter;

    public static void main(String[] args) {
        try {
            File file = new File(AgentClient.class.getProtectionDomain().getCodeSource().getLocation().getPath());
            if (file.isDirectory()) { // Running from IDE
                WordipleClient.main(args);
                return;
            }
            List<String> argsList = Arrays.asList(args);
            setupLogger(Arrays.asList(args).contains("-developer"));
            String name = file.getName().replace("%20", " ");
            log("Using " + name);
            String OS = (System.getProperty("os.name")).toUpperCase();
            if (OS.contains("WIN")) {
                name = "\"" + name + "\"";
            }
            String module = argsList.contains("-module") ? argsList.get(argsList.indexOf("-module") + 1) : "";
            String command = "java -javaagent:" + name + " " + (module.isEmpty() ? "" : "--module-path " + module + " --add-modules javafx.controls,javafx.fxml ") + "-cp " + name + " me.logicologist.wordiple.client.WordipleClient " + String.join(" ", args);
            log("Executing " + command);
            Process process = Runtime.getRuntime().exec(command);
            new Thread(() -> {
                BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line;
                try {
                    while ((line = input.readLine()) != null) {
                        log(line);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
            new Thread(() -> {
                BufferedReader error = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                String line;
                try {
                    while ((line = error.readLine()) != null) {
                        log(line);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
            process.waitFor();
            logWriter.close();
            System.exit(0);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static void log(String log) {
        try {
            System.out.println(log);
            logWriter.write(log + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void setupLogger(boolean developerMode) {
        try {
            WordipleClient.developerMode = developerMode;
            File file = new File(WordipleClient.getAppData(), "latest.log");
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            file.delete();
            file.createNewFile();
            logWriter = new FileWriter(file.getAbsolutePath(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
