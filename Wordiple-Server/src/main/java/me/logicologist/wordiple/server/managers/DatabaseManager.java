package me.logicologist.wordiple.server.managers;

import com.google.common.hash.Hashing;
import com.olziedev.olziesocket.framework.api.packet.PacketHolder;
import me.logicologist.wordiple.server.user.WordipleUser;

import java.io.File;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Random;
import java.util.UUID;

public class DatabaseManager {

    public static DatabaseManager instance;
    private Connection connection;
    private final File databaseFile = new File("data/database.db");

    public DatabaseManager() {
        instance = this;
    }

    public void setup() {
        try {
            if (!databaseFile.exists()) {
                databaseFile.getParentFile().mkdirs();
                databaseFile.createNewFile();
            }
            this.getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS users(" +
                    "email VARCHAR(255) COLLATE NOCASE," +
                    "uuid VARCHAR(255)," +
                    "username VARCHAR(255) COLLATE NOCASE," +
                    "passwordhash VARCHAR(255)," +
                    "passwordsalt VARCHAR(255)," +
                    "rating INTEGER," +
                    "level INTEGER, " +
                    "experience INTEGER," +
                    "wins INTEGER," +
                    "games_played INTEGER," +
                    "playtime LONG," +
                    "bannedtime LONG," +
                    "competitiveban BOOLEAN," +
                    "onlineban BOOLEAN," +
                    "globalban BOOLEAN," +
                    "PRIMARY KEY(email))"
            ).executeUpdate();
            this.getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS replays(" +
                    "uuid VARCHAR(255)," +
                    "replaystring LONGTEXT," +
                    "participants LONGTEXT," +
                    "PRIMARY KEY(uuid))"
            ).executeUpdate();
            this.getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS reports(" +
                    "uuid VARCHAR(255)," +
                    "reported VARCHAR(255)," +
                    "reporter VARCHAR(255)," +
                    "reason LONGTEXT," +
                    "PRIMARY KEY(uuid))"
            ).executeUpdate();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void connect() throws Exception {
        Class.forName("org.sqlite.JDBC");
        this.connection = DriverManager.getConnection("jdbc:sqlite:" + databaseFile.getAbsolutePath());
    }

    public Connection getConnection() throws Exception {
        if (this.connection == null || this.connection.isClosed()) this.connect();
        return this.connection;
    }

    public boolean validateLogin(String username, String password) {
        try {
            PreparedStatement ps = getConnection().prepareStatement("SELECT passwordhash, passwordsalt FROM users WHERE username=?");
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) return false;
            String saltedPassword = password + rs.getString("passwordsalt");
            String salted = Hashing.sha256().hashString(saltedPassword, Charset.defaultCharset()).toString();
            return salted.equals(rs.getString("passwordhash"));
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public void setPassword(String email, String password) {
        try {
            StringBuilder salt = new StringBuilder();
            String saltChars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
            for (int i = 0; i < 16; i++) {
                salt.append(saltChars.charAt(new Random().nextInt(saltChars.length())));
            }
            String hashed = Hashing.sha256().hashString(password + salt, Charset.defaultCharset()).toString();
            PreparedStatement ps = getConnection().prepareStatement("UPDATE users SET passwordhash = ?, passwordsalt = ? WHERE email = ?");
            ps.setString(1, hashed);
            ps.setString(2, salt.toString());
            ps.setString(3, email);
            ps.executeUpdate();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public WordipleUser constructWordipleUser(String username, PacketHolder socket) {
        try {
            PreparedStatement ps = getConnection().prepareStatement("SELECT * FROM users WHERE username=?");
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) return null;
            return new WordipleUser(
                    rs.getString("email"),
                    UUID.fromString(rs.getString("uuid")),
                    rs.getString("username"),
                    rs.getInt("rating"),
                    rs.getInt("highest_rating"),
                    rs.getInt("level"),
                    rs.getInt("experience"),
                    rs.getInt("wins"),
                    rs.getInt("games_played"),
                    rs.getLong("playtime"),
                    rs.getLong("bannedtime"),
                    rs.getBoolean("competitiveban"),
                    rs.getBoolean("onlineban"),
                    rs.getBoolean("globalban"),
                    rs.getInt("guesses"),
                    rs.getInt("rank"),
                    rs.getInt("highest_rank"),
                    socket
            );
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public void saveUser(WordipleUser user) {
        try {
            user.setPlaytime(user.getPlaytime() + (user.getLoggedInTime() == null ? 0 : System.currentTimeMillis() - user.getLoggedInTime().getTime()));
            user.setLoggedInTime(null);
            PreparedStatement ps = getConnection().prepareStatement("UPDATE users SET rating=?, highest_rating =?, level=?, experience=?, wins=?, games_played=?, playtime=?, bannedtime=?, competitiveban=?, onlineban=?, globalban=?, guesses=?, rank=?, highest_rank=? WHERE uuid=?");
            ps.setInt(1, user.getRating());
            ps.setInt(2, user.getHighestRating());
            ps.setInt(3, user.getLevel());
            ps.setInt(4, user.getExperience());
            ps.setInt(5, user.getWins());
            ps.setInt(6, user.getGamesPlayed());
            ps.setLong(7, user.getPlaytime());
            ps.setLong(8, user.getBannedTime());
            ps.setBoolean(9, user.isCompetitiveBan());
            ps.setBoolean(10, user.isOnlineBan());
            ps.setBoolean(11, user.isGlobalBan());
            ps.setInt(11, user.getGuesses());
            ps.setInt(12, user.getRank());
            ps.setInt(13, user.getHighestRank());
            ps.setString(14, user.getId().toString());
            ps.executeUpdate();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void createUser(WordipleUser user, String password) {
        StringBuilder salt = new StringBuilder();
        String saltChars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        for (int i = 0; i < 16; i++) {
            salt.append(saltChars.charAt(new Random().nextInt(saltChars.length())));
        }
        String hashed = Hashing.sha256().hashString(password + salt, Charset.defaultCharset()).toString();
        try {
            Connection con = getConnection();
            PreparedStatement ps = con.prepareStatement("INSERT INTO users (uuid, username, email, passwordhash, passwordsalt, rating, level, experience, wins, games_played, playtime, bannedtime, competitiveban, onlineban, globalban) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
            ps.setString(1, user.getId().toString());
            ps.setString(2, user.getUsername());
            ps.setString(3, user.getEmail());
            ps.setString(4, hashed);
            ps.setString(5, salt.toString());
            ps.setInt(6, 0);
            ps.setInt(7, 0);
            ps.setInt(8, 0);
            ps.setInt(9, 0);
            ps.setInt(10, 0);
            ps.setLong(11, 0);
            ps.setLong(12, 0);
            ps.setBoolean(13, false);
            ps.setBoolean(14, false);
            ps.setBoolean(15, false);
            ps.executeUpdate();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public UUID generateNewId() {
        UUID potentialUUID = UUID.randomUUID();
        try {
            Connection con = getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT * FROM users WHERE uuid=?");
            ps.setString(1, potentialUUID.toString());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return generateNewId();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return potentialUUID;
    }

    public boolean usernameAvailable(String username) {
        try {
            Connection con = getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT * FROM users WHERE username=?");
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            return !rs.next();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public boolean emailAvailable(String email) {
        try {
            Connection con = getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT * FROM users WHERE email=?");
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            return !rs.next();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public String getUsername(String email) {
        try {
            Connection con = getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT username FROM users WHERE email=?");
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) return null;

            return rs.getString("username");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public UUID getUUID(String email) {
        try {
            Connection con = getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT uuid FROM users WHERE email=?");
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) return null;

            return UUID.fromString(rs.getString("uuid"));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
}
