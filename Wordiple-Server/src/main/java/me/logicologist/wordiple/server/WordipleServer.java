package me.logicologist.wordiple.server;

import me.logicologist.wordiple.server.managers.DatabaseManager;
import me.logicologist.wordiple.server.managers.PacketManager;
import me.logicologist.wordiple.server.managers.SessionManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class WordipleServer {

    private static final Logger logger = LogManager.getLogger();
    private static final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

    public static void main(String[] args) {
        new PacketManager().load();
        new DatabaseManager().setup();
        new SessionManager();
    }

    public static ScheduledExecutorService getExecutor() {
        return executor;
    }

    public static Logger getLogger() {
        return logger;
    }
}
