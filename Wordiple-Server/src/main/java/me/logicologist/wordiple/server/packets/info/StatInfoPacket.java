package me.logicologist.wordiple.server.packets.info;

import com.olziedev.olziesocket.framework.PacketArguments;
import com.olziedev.olziesocket.framework.api.packet.PacketAdapter;
import com.olziedev.olziesocket.framework.api.packet.PacketType;
import me.logicologist.wordiple.common.utils.Utils;
import me.logicologist.wordiple.server.WordipleServer;
import me.logicologist.wordiple.server.managers.SessionManager;
import me.logicologist.wordiple.server.rank.RankRange;
import me.logicologist.wordiple.server.user.WordipleUser;

public class StatInfoPacket extends PacketAdapter implements PacketType {

    public StatInfoPacket() {
        super("stat_info_packet");
        this.packetType = this;
    }

    @Override
    public boolean onlySendToServer() {
        return true;
    }

    @Override
    public void onReceive(PacketArguments arguments) {
        WordipleUser user = SessionManager.getInstance().getSessionFromUsername(arguments.get("username", String.class));
        WordipleServer.getLogger().info("Received stat info packet from " + user);
        if (user == null) return; // THIS SHOULD NEVER HAPPEN, BUT JUST IN CASE!

        long currentSession = System.currentTimeMillis() - user.getLoggedInTime().getTime();
        RankRange rankRange = RankRange.getInstance();
        this.sendPacket(packet -> arguments.replace(this.getArguments())
                .setValues("username", user.getUsername())
                .setValues("games_played", Utils.formatNumber(user.getGamesPlayed()))
                .setValues("wins", Utils.formatNumber(user.getWins()))
                .setValues("losses", Utils.formatNumber(user.getGamesPlayed() - user.getWins()))
                .setValues("playtime", Utils.formatShortTime((user.getPlaytime() + currentSession) / 1000))
                .setValues("current_session", Utils.formatShortTime(currentSession / 1000))
                .setValues("playtime_raw", (user.getPlaytime() + currentSession) / 1000)
                .setValues("current_session_raw", currentSession / 1000)
                .setValues("season", "Season 1")
                .setValues("current_rank", rankRange.getRank(user.getRating()).getName())
                .setValues("current_rating", Utils.formatNumber(user.getRating()))
                .setValues("highest_rank", rankRange.getRank(user.getHighestRating()).getName())
                .setValues("highest_rating", Utils.formatNumber(user.getHighestRating()))
                .setValues("solve_time", user.getAverageSolveTime() + "s")
                .setValues("opener", user.getMostUsedOpener())
                .setValues("level", Utils.formatNumber(user.getLevel()))
                .setValues("total_xp", Utils.formatNumber(user.getTotalExperience()))
                .setValues("guesses", Utils.formatNumber(user.getAverageGuesses())));
    }

    @Override
    public PacketArguments getArguments() {
        return new PacketArguments()
                .setArgument("username", String.class)
                .setArgument("games_played", String.class)
                .setArgument("wins", String.class)
                .setArgument("losses", String.class)
                .setArgument("playtime", String.class)
                .setArgument("current_session", String.class)
                .setArgument("playtime_raw", Long.class)
                .setArgument("current_session_raw", Long.class)
                .setArgument("season", String.class)
                .setArgument("current_rank", String.class)
                .setArgument("current_rating", String.class)
                .setArgument("highest_rank", String.class)
                .setArgument("highest_rating", String.class)
                .setArgument("solve_time", String.class)
                .setArgument("opener", String.class)
                .setArgument("level", String.class)
                .setArgument("total_xp", String.class)
                .setArgument("guesses", String.class);
    }
}
