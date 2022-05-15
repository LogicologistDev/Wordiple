package me.logicologist.wordiple.client;

import java.io.File;
import java.io.IOException;

public class AgentClient {

    public static void main(String[] args) {
        try {
            File file = new File(AgentClient.class.getProtectionDomain().getCodeSource().getLocation().getPath());
            if (file.isDirectory()) { // Running from IDE
                WordipleClient.main(args);
                return;
            }
            Runtime.getRuntime().exec("java -javaagent:\"" + file.getName() + "\" -cp \"" + file.getName() + "\" me.logicologist.wordiple.client.WordipleClient").waitFor();
            System.exit(0);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
