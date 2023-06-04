package me.logicologist.wordiple.server;

import me.logicologist.wordiple.server.managers.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class WordipleServer {

    private static final Logger logger = LogManager.getLogger(WordipleServer.class);
    private static final ScheduledExecutorService executor = Executors.newScheduledThreadPool(10);

    public static void main(String[] args) {
        new WordManager();
        new PacketManager().load();
        new DatabaseManager().setup();
        new SessionManager();
        new QueueManager().load();
        new MatchManager();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Shutting down...");
            executor.shutdownNow();
            SessionManager.getInstance().close();
            PacketManager.getInstance().getSocket().shutdownServer();
        }));
    }

    public static ScheduledExecutorService getExecutor() {
        return executor;
    }

    public static Logger getLogger() {
        return logger;
    }
}
