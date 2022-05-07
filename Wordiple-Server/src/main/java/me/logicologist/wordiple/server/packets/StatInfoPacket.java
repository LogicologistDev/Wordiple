package me.logicologist.wordiple.server.packets;

import com.olziedev.olziesocket.framework.PacketArguments;
import com.olziedev.olziesocket.framework.api.packet.PacketAdapter;
import me.logicologist.wordiple.common.packets.AuthPacketType;
import me.logicologist.wordiple.common.utils.Utils;
import me.logicologist.wordiple.server.WordipleServer;
import me.logicologist.wordiple.server.managers.SessionManager;
import me.logicologist.wordiple.server.user.WordipleUser;

public class StatInfoPacket extends PacketAdapter implements AuthPacketType {

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
        WordipleUser user = SessionManager.getInstance().getSessionFromToken(this.getSessionID(arguments));
        WordipleServer.getLogger().info("Received stat info packet from " + user);
        if (user == null) return; // THIS SHOULD NEVER HAPPEN, BUT JUST IN CASE!

        this.sendPacket(packet -> arguments.replace(this.getArguments())
                .setValues("username", user.getUsername())
                .setValues("games_played", Utils.formatNumber(user.getGamesPlayed()))
                .setValues("wins", Utils.formatNumber(user.getWins()))
                .setValues("losses", Utils.formatNumber(user.getGamesPlayed() - user.getWins()))
                .setValues("playtime", Utils.formatShortTime(user.getPlaytime() / 1000))
                .setValues("curren_session", Utils.formatShortTime((System.currentTimeMillis() - user.getLoggedInTime().getTime()) / 1000))
                .setValues("season", "")
                .setValues("current_rank", "")
                .setValues("current_rating", "")
                .setValues("highest_rank", "")
                .setValues("highest_rating", "")
                .setValues("solve_time", "")
                .setValues("opener", "")
                .setValues("level", Utils.formatNumber(user.getLevel()))
                .setValues("total_xp", Utils.formatNumber(user.getExperience()))
                .setValues("guesses", ""));
    }

    @Override
    public PacketArguments getArguments() {
        return new PacketArguments()
                .setArgument("username", String.class)
                .setArgument("games_played", String.class)
                .setArgument("wins", String.class)
                .setArgument("losses", String.class)
                .setArgument("playtime", String.class)
                .setArgument("curren_session", String.class)
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
