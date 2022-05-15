package me.logicologist.wordiple.client;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class AgentClient {

    public static void main(String[] args) {
        try {
            File file = new File(AgentClient.class.getProtectionDomain().getCodeSource().getLocation().getPath());
            if (file.isDirectory()) { // Running from IDE
                WordipleClient.main(args);
                return;
            }
            String name = file.getName().replace("%20", " ");
            System.out.println("Running " + name + "...");
            String command = "java -javaagent:\"" + name + "\" -cp \"" + name + "\" me.logicologist.wordiple.client.WordipleClient " + String.join(" ", args);
            System.out.println("Executing: " + command);
            Process process = Runtime.getRuntime().exec(command);
            new Thread(() -> {
                BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line;
                try {
                    while ((line = input.readLine()) != null)
                        System.out.println(line);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
            process.waitFor();
            System.exit(0);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
