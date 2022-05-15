package me.logicologist.wordiple.client.manager;

import com.olziedev.olziesocket.framework.PacketArguments;
import me.logicologist.wordiple.client.WordipleClient;
import me.logicologist.wordiple.client.gui.controllers.select.PlayerHeaderController;
import me.logicologist.wordiple.client.packets.auth.LogoutPacket;
import me.logicologist.wordiple.common.packets.AuthPacketType;

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
    private int rating;
    private String rank;
    private String username;
    private String version;


    public SessionManager() {
        this.file = new File(WordipleClient.getAppData().getAbsolutePath(), "session.properties");
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

    public String getLocalAssetVersion() {
        try {
            Properties properties = new Properties();
            properties.load(Files.newInputStream(file.toPath()));
            return properties.getProperty("assetVersion");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public void setLocalSessionID(UUID localSessionID, boolean sendPacket) {
        try {
            this.loggedIn = localSessionID != null;
            Properties properties = new Properties();
            properties.load(Files.newInputStream(file.toPath()));
            if (localSessionID == null) {
                PlayerHeaderController.instance = null;
                if (sendPacket) {
                    PacketManager.getInstance().getSocket().getPacket(LogoutPacket.class).sendPacket(packet ->
                            packet.getPacketType(AuthPacketType.class).getArguments(this.getLocalSessionID()).setValues("logout", true));
                }
                properties.remove("sessionID");
            }
            if (localSessionID != null) {
                properties.setProperty("sessionID", localSessionID.toString());
            }
            properties.store(Files.newOutputStream(file.toPath()), "Please do not share/touch this file.");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void setAssetVersion(String localSoundVersion) {
        try {
            Properties properties = new Properties();
            properties.load(Files.newInputStream(file.toPath()));
            properties.setProperty("assetVersion", localSoundVersion);
            properties.store(Files.newOutputStream(file.toPath()), "Please do not share/touch this file.");
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

    public int getRating() {
        return rating;
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

    public void setRating(int rating) {
        this.rating = rating;
    }

    public void load(PacketArguments arguments, String username) {
        this.setCurrentXp(arguments.get("xp", Integer.class));
        this.setNeededXp(arguments.get("neededXp", Integer.class));
        this.setLevel(arguments.get("level", Integer.class));
        this.setRating(arguments.get("rating", Integer.class));
        this.setRank(arguments.get("rank", String.class));
        this.setUsername(username);
        this.setLoggedIn(true);
        this.setVersion(arguments.get("version", String.class));
    }

    private void setRank(String rank) {
        this.rank = rank;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getVersion() {
        return this.version;
    }

    public static SessionManager getInstance() {
        return instance;
    }

    public String getRank() {
        return rank;
    }
}
