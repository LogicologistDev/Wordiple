package me.logicologist.wordiple.client;

import me.logicologist.wordiple.client.manager.GUIManager;
import me.logicologist.wordiple.client.manager.PacketManager;
import me.logicologist.wordiple.client.manager.SessionManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class WordipleClient {

    private static File appData;
    private static final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
    private static final Logger logger = LogManager.getLogger();

    public static void main(String[] args) {
        boolean developerMode = Arrays.asList(args).contains("-developer");
        new SessionManager(developerMode);
        new PacketManager(developerMode).load();
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
        logger.info("AppData: " + appData.getAbsolutePath());
        return appData;
    }

    public static ScheduledExecutorService getExecutor() {
        return executor;
    }

    public static Logger getLogger() {
        return logger;
    }
}
