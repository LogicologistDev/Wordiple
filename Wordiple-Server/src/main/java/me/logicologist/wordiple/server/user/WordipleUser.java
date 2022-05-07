package me.logicologist.wordiple.server.user;

import com.olziedev.olziesocket.framework.api.packet.PacketHolder;
import me.logicologist.wordiple.server.managers.DatabaseManager;
import me.logicologist.wordiple.server.managers.PacketManager;

import java.io.ObjectOutputStream;
import java.util.Date;
import java.util.UUID;

public class WordipleUser {

    private final String email;
    private final UUID id;
    private String username;
    private int rating;
    private int level;
    private int experience;
    private int wins;
    private int gamesPlayed;
    private long playtime;
    private Date loggedInTime;
    private long bannedTime;
    private boolean competitiveBan;
    private boolean onlineBan;
    private boolean globalBan;
    private int guesses;
    private PacketHolder socket;

    public WordipleUser(String email, UUID id, String username, int rating, int level, int experience, int wins, int gamesPlayed, long playtime, long bannedTime, boolean competitiveBan, boolean onlineBan, boolean globalBan, PacketHolder socket) {
        this.email = email;
        this.id = id;
        this.username = username;
        this.rating = rating;
        this.level = level;
        this.experience = experience;
        this.wins = wins;
        this.gamesPlayed = gamesPlayed;
        this.playtime = playtime;
        this.bannedTime = bannedTime;
        this.competitiveBan = competitiveBan;
        this.onlineBan = onlineBan;
        this.globalBan = globalBan;
        this.socket = socket;
    }

    public WordipleUser(String email, String username) {
        this(email, DatabaseManager.instance.generateNewId(), username, 0, 0, 0, 0, 0, 0, 0, false, false, false, null);
    }

    // Generate getters and setters
    public String getEmail() {
        return email;
    }

    public UUID getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getNeededExperience() {
        if (level == 0) {
            return 100;
        }
        return Math.min((int) ((Math.pow(Math.log(this.level * 10) / Math.log(2), 2)) * Math.sqrt(this.level) + 100), 2000);
    }

    public int getExperience() {
        return experience;
    }

    public void setExperience(int experience) {
        this.experience = experience;
    }

    public int getWins() {
        return wins;
    }

    public void setWins(int wins) {
        this.wins = wins;
    }

    public int getGamesPlayed() {
        return gamesPlayed;
    }

    public void setGamesPlayed(int gamesPlayed) {
        this.gamesPlayed = gamesPlayed;
    }

    public long getPlaytime() {
        return playtime;
    }

    public void setPlaytime(long playtime) {
        this.playtime = playtime;
    }

    public long getBannedTime() {
        return bannedTime;
    }

    public void setBannedTime(long bannedTime) {
        this.bannedTime = bannedTime;
    }

    public boolean isCompetitiveBan() {
        return competitiveBan;
    }

    public void setCompetitiveBan(boolean competitiveBan) {
        this.competitiveBan = competitiveBan;
    }

    public boolean isOnlineBan() {
        return onlineBan;
    }

    public void setOnlineBan(boolean onlineBan) {
        this.onlineBan = onlineBan;
    }

    public boolean isGlobalBan() {
        return globalBan;
    }

    public void setGlobalBan(boolean globalBan) {
        this.globalBan = globalBan;
    }

    public int getGuesses() {
        return 0;
    }

    public void setGuesses(int guesses) {

    }

    public ObjectOutputStream getOutputStream() {
        try {
            return PacketManager.getInstance().getSocket().getOutputStream(this.socket);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public void setSocket(PacketHolder socket) {
        this.socket = socket;
    }

    public Date getLoggedInTime() {
        return loggedInTime;
    }

    public void setLoggedInTime(Date loggedInTime) {
        this.loggedInTime = loggedInTime;
    }

    public double getTotalExperience() {
        int total = this.experience;
        for (int i = 0; i < this.level; i++) {
            if (i == 0) {
                total += 100;
            }
            total += Math.min((int) ((Math.pow(Math.log(i * 10) / Math.log(2), 2)) * Math.sqrt(i) + 100), 2000);
        }
        return total;
    }
}
