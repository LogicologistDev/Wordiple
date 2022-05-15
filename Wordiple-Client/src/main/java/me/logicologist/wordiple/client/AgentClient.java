package me.logicologist.wordiple.client;

import java.io.*;

public class AgentClient {

    private static FileWriter logWriter;

    public static void main(String[] args) {
        try {
            File file = new File(AgentClient.class.getProtectionDomain().getCodeSource().getLocation().getPath());
            if (file.isDirectory()) { // Running from IDE
                WordipleClient.main(args);
                return;
            }
            setupLogger();
            String name = file.getName().replace("%20", " ");
            log("Running " + name);
            String OS = (System.getProperty("os.name")).toUpperCase();
            if (OS.contains("WIN")) {
                name = "\"" + name + "\"";
            }
            String command = "java -javaagent:" + name + " -cp " + name + " me.logicologist.wordiple.client.WordipleClient";
            log("Running " + command);
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

    private static void setupLogger() {
        try {
            File file = new File(WordipleClient.getAppData(), "latest.log");
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            file.delete();
            file.createNewFile();
            logWriter = new FileWriter("log.txt", true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
