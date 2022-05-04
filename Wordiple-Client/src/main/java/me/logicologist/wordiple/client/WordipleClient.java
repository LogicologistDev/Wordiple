package me.logicologist.wordiple.client;

import me.logicologist.wordiple.client.manager.GUIManager;
import me.logicologist.wordiple.client.manager.PacketManager;
import me.logicologist.wordiple.client.manager.SessionManager;

import java.io.File;

public class WordipleClient {

    private static File appData;

    public static void main(String[] args) {
        new SessionManager();
        new PacketManager().load();
        GUIManager.launch(args);
    }

    public static File getAppData() {
        if (appData != null) return appData;

        String OS = (System.getProperty("os.name")).toUpperCase();
        String workingDirectory;
        if (OS.contains("WIN")) {
            workingDirectory = System.getenv("AppData");
        } else {
            workingDirectory = System.getProperty("user.home");
            workingDirectory += File.separator + "Library" + File.separator + "Application Support";
        }

        appData = new File(workingDirectory + File.separator + "Wordiple");
        return appData;
    }
}
