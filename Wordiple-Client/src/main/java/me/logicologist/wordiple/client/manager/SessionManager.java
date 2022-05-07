package me.logicologist.wordiple.client.manager;

import com.olziedev.olziesocket.framework.PacketArguments;
import com.olziedev.olziesocket.framework.api.packet.PacketAdapter;
import me.logicologist.wordiple.client.WordipleClient;
import me.logicologist.wordiple.client.gui.controllers.PlayerHeaderController;

import java.io.File;
import java.nio.file.Files;
import java.util.Properties;
import java.util.UUID;

public class SessionManager {

    private static SessionManager instance;
    private boolean loggedIn;
    private final File file;
    private int level;
    private int currentXp;
    private int neededXp;
    private String username;


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
            if (localSessionID == null) {
                this.loggedIn = false;
                PlayerHeaderController.instance = null;
            }
            Properties properties = new Properties();
            if (localSessionID != null) {
                properties.setProperty("sessionID", localSessionID.toString());
                this.loggedIn = true;
            }
            properties.store(Files.newOutputStream(file.toPath()), "Please do not give share/touch this file.");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public boolean isLoggedIn() {
        return this.loggedIn;
    }

    public int getLevel() {
        return level;
    }

    public int getCurrentXp() {
        return currentXp;
    }

    public int getNeededXp() {
        return neededXp;
    }

    public String getUsername() {
        return username;
    }

    public void setLoggedIn(boolean loggedIn) {
        this.loggedIn = loggedIn;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void setCurrentXp(int currentXp) {
        this.currentXp = currentXp;
    }

    public void setNeededXp(int neededXp) {
        this.neededXp = neededXp;
    }

    public void load(PacketArguments arguments, String username) {
        this.setCurrentXp(arguments.get("xp", Integer.class));
        this.setNeededXp(arguments.get("neededXp", Integer.class));
        this.setLevel(arguments.get("level", Integer.class));
        this.setUsername(username);
        this.setLoggedIn(true);
    }

    public static SessionManager getInstance() {
        return instance;
    }
}
