package me.logicologist.wordiple.server.managers;

import com.olziedev.olziesocket.framework.api.packet.PacketHolder;
import me.logicologist.wordiple.server.user.WordipleUser;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.UUID;
import java.util.regex.Pattern;

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
                    "highest_rating INTEGER" +
                    "level INTEGER, " +
                    "experience INTEGER," +
                    "wins INTEGER," +
                    "games_played INTEGER," +
                    "playtime LONG," +
                    "bannedtime LONG," +
                    "competitiveban BOOLEAN," +
                    "onlineban BOOLEAN," +
                    "globalban BOOLEAN," +
                    "solvetimes VARCHAR(255)," +
                    "guesses VARCHAR(255)" +
                    "openers VARCHAR(255)," +
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

    public boolean validateLogin(String username, String passwordHash) {
        try {
            PreparedStatement ps = getConnection().prepareStatement("SELECT passwordhash FROM users WHERE username=?");
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) return false;
            return passwordHash.equals(rs.getString("passwordhash"));
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public boolean setPassword(String email, String salt, String hashed) {
        Pattern validSalt = Pattern.compile("^[a-zA-Z0-9]{16}$");
        if (!validSalt.matcher(salt).matches()) return false;
        try {
            PreparedStatement ps = getConnection().prepareStatement("UPDATE users SET passwordhash = ?, passwordsalt = ? WHERE email = ?");
            ps.setString(1, hashed);
            ps.setString(2, salt.toString());
            ps.setString(3, email);
            ps.executeUpdate();
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
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
                    rs.getString("guesses"),
                    rs.getString("solve_times"),
                    rs.getString("openers"),
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
            PreparedStatement ps = getConnection().prepareStatement("UPDATE users SET rating=?, highest_rating =?, level=?, experience=?, wins=?, games_played=?, playtime=?, bannedtime=?, competitiveban=?, onlineban=?, globalban=?, guesses=?, solvetimes=?, guesses=?, openers=? WHERE uuid=?");
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
            ps.setString(12, user.getId().toString());
            ps.setString(13, user.getSolveTimesAsString());
            ps.setString(14, user.getGuessesAsString());
            ps.setString(15, user.getOpenersAsString());
            ps.executeUpdate();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void createUser(WordipleUser user, String hashed, String salt) {
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

    public String getSalt(String username) {
        try {
            Connection con = getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT passwordsalt FROM users WHERE username=?");
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) return null;

            return rs.getString("passwordsalt");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
}
