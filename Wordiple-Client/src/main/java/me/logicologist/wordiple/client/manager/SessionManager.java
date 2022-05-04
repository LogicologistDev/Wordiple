package me.logicologist.wordiple.client.manager;

import me.logicologist.wordiple.client.WordipleClient;

import java.io.File;
import java.nio.file.Files;
import java.util.Properties;
import java.util.UUID;

public class SessionManager {

    private static SessionManager instance;

    private final File file;

    public SessionManager(boolean developer) {
        this.file = new File(developer ? "data" : WordipleClient.getAppData().getAbsolutePath(), "session.properties");
        if (!file.getParentFile().exists()) file.getParentFile().mkdirs();

        try {
            file.createNewFile();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        instance = this;
    }

    public UUID getLocalSessionID() {
        try {
            Properties properties = new Properties();
            properties.load(Files.newInputStream(file.toPath()));
            String id = properties.getProperty("sessionID");
            return id == null ? null : UUID.fromString(id);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public void setLocalSessionID(UUID localSessionID) {
        try {
            Properties properties = new Properties();
            if (localSessionID != null) properties.setProperty("sessionID", localSessionID.toString());
            properties.store(Files.newOutputStream(file.toPath()), "Please do not give share/touch this file.");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static SessionManager getInstance() {
        return instance;
    }
}
