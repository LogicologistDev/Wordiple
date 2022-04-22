package me.logicologist.wordiple.server.managers;

import com.google.common.hash.Hashing;
import me.logicologist.wordiple.server.user.WordipleUser;

import java.io.File;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
            this.getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS users(" +
                    "email VARCHAR(255)," +
                    "uuid VARCHAR(255)" +
                    "username VARCHAR(255)," +
                    "passwordhash VARCHAR(255)," +
                    "passwordsalt VARCHAR(255)," +
                    "rating INTEGER," +
                    "level INTEGER, " +
                    "experience INTEGER," +
                    "wins INTEGER," +
                    "games_played INTEGER," +
                    "playtime LONG," +
                    "bannedtime LONG" +
                    "competitiveban BOOLEAN" +
                    "onlineban BOOLEAN," +
                    "globalban BOOLEAN" +
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
                    "reported VARCHAR(255))," +
                    "reporter VARCHAR(255)," +
                    "reason LONGTEXT," +
                    "PRIMARY KEY(uuid)"
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

    public WordipleUser constructWordipleUser(String username) {
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
                    rs.getInt("level"),
                    rs.getInt("experience"),
                    rs.getInt("wins"),
                    rs.getInt("games_played"),
                    rs.getLong("playtime"),
                    rs.getLong("bannedtime"),
                    rs.getBoolean("competitiveban"),
                    rs.getBoolean("onlineban"),
                    rs.getBoolean("globalban")
            );
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
}
