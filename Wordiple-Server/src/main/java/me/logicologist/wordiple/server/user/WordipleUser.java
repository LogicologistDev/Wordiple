package me.logicologist.wordiple.server.user;

import com.olziedev.olziesocket.framework.PacketArguments;
import me.logicologist.wordiple.server.managers.DatabaseManager;
import me.logicologist.wordiple.server.managers.PacketManager;
import me.logicologist.wordiple.server.rank.RankRange;

import java.io.ObjectOutputStream;
import java.util.*;

public class WordipleUser {

    private final String email;
    private final UUID id;
    private String username;
    private int rating;
    private int highestRating;
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
    private PacketArguments.PacketHolder socket;
    private List<Double> solveTimes;
    private List<Integer> guesses;
    private List<String> openers;

    public WordipleUser(String email, UUID id, String username, int rating, int highestRating, int level, int experience, int wins, int gamesPlayed, long playtime, long bannedTime, boolean competitiveBan, boolean onlineBan, boolean globalBan, String guesses, String solveTimes, String openers, PacketArguments.PacketHolder socket) {
        this.email = email;
        this.id = id;
        this.username = username;
        this.rating = rating;
        this.highestRating = highestRating;
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
        this.guesses = new ArrayList<Integer>() {{
            if (guesses != null && !guesses.isEmpty()) {
                for (String guess : guesses.split(",")) {
                    add(Integer.parseInt(guess));
                }
            }
        }};
        this.solveTimes = new ArrayList<Double>() {{
            if (solveTimes != null && !solveTimes.isEmpty()) {
                for (String solveTime : solveTimes.split(",")) {
                    add(Double.parseDouble(solveTime));
                }
            }
        }};
        this.openers = new ArrayList<String>() {{
            if (openers != null && !openers.isEmpty()) this.addAll(Arrays.asList(openers.split(",")));
        }};
    }

    public WordipleUser(String email, String username) {
        this(email, DatabaseManager.instance.generateNewId(), username, 0, 0, 0, 0, 0, 0, 0, 0, false, false, false, "", "", "", null);
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
        if (rating < 0) rating = 0;
        if (highestRating < rating) highestRating = rating;
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
        while (this.experience >= this.getNeededExperience()) {
            this.level++;
            this.experience -= this.getNeededExperience();
        }
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

    public double getAverageSolveTime() {
        double sum = 0;
        for (double solveTime : solveTimes) {
            sum += solveTime;
        }
        return Math.round(sum / solveTimes.size() * 100.0) / 100.0;
    }
    public double getAverageGuesses() {
        double sum = 0;
        for (int guess : guesses) {
            sum += guess;
        }
        return Math.round(sum / guesses.size() * 100.0) / 100.0;
    }

    public String getMostUsedOpener() {
        Map<String, Integer> openerMap = new HashMap<>();
        for (String opener : openers) {
            if (openerMap.containsKey(opener)) {
                openerMap.put(opener, openerMap.get(opener) + 1);
            } else {
                openerMap.put(opener, 1);
            }
        }
        int max = 0;
        String mostUsedOpener = "N/A";
        for (String opener : openerMap.keySet()) {
            if (openerMap.get(opener) > max) {
                max = openerMap.get(opener);
                mostUsedOpener = opener;
            }
        }
        return mostUsedOpener;
    }

    public String getSolveTimesAsString() {
        StringBuilder sb = new StringBuilder();
        for (double solveTime : solveTimes) {
            sb.append(solveTime).append(",");
        }
        return sb.substring(0, (sb.length() != 0 ? sb.length() - 1 : 0));
    }

    public String getGuessesAsString() {
        StringBuilder sb = new StringBuilder();
        for (int guess : guesses) {
            sb.append(guess).append(",");
        }
        return sb.substring(0, (sb.length() != 0 ? sb.length() - 1 : 0));
    }

    public String getOpenersAsString() {
        StringBuilder sb = new StringBuilder();
        for (String opener : openers) {
            sb.append(opener).append(",");
        }
        return sb.substring(0, (sb.length() != 0 ? sb.length() - 1 : 0));
    }

    public void addGuess(int guesses) {
        if (this.guesses.size() >= 20) {
            this.guesses.remove(0);
        }
        this.guesses.add(guesses);
    }

    public void addSolveTime(double solveTime) {
        if (this.solveTimes.size() >= 20) {
            this.solveTimes.remove(0);
        }
        this.solveTimes.add(solveTime);
    }

    public void addOpener(String opener) {
        if (this.openers.size() >= 20) {
            this.openers.remove(0);
        }
        this.openers.add(opener);
    }

    public ObjectOutputStream getOutputStream() {
        if (this.socket == null) return null;

        try {
            return PacketManager.getInstance().getSocket().getOutputStream(this.socket);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public void setSocket(PacketArguments.PacketHolder socket) {
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

    public int getHighestRating() {
        return this.highestRating;
    }

}
