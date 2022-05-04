package me.logicologist.wordiple.server.packets;

import com.olziedev.olziesocket.framework.PacketArguments;
import com.olziedev.olziesocket.framework.api.packet.PacketAdapter;
import com.olziedev.olziesocket.framework.api.packet.PacketType;
import me.logicologist.wordiple.server.managers.SessionManager;
import me.logicologist.wordiple.server.user.WordipleUser;

import java.util.UUID;

public class SessionCheckPacket extends PacketAdapter implements PacketType {

    public SessionCheckPacket() {
        super("session_check_packet");
        this.packetType = this;
    }

    @Override
    public void onReceive(PacketArguments arguments) {
        SessionManager sessionManager = SessionManager.getInstance();
        WordipleUser wordipleUser = sessionManager.getSessionFromToken(arguments.get("token", UUID.class));
        this.sendPacket(packet -> arguments.replace(this.getArguments())
                .setValues("email", wordipleUser.getEmail())
                .setValues("id", wordipleUser.getId())
                .setValues("username", wordipleUser.getUsername())
                .setValues("rating", wordipleUser.getRating())
                .setValues("level", wordipleUser.getLevel())
                .setValues("xp", wordipleUser.getExperience())
                .setValues("wins", wordipleUser.getWins())
                .setValues("bannedTime", wordipleUser.getBannedTime())
                .setValues("playtime", wordipleUser.getPlaytime())
                .setValues("competitiveBan", wordipleUser.isCompetitiveBan())
                .setValues("onlineBan", wordipleUser.isOnlineBan())
                .setValues("globalBan", wordipleUser.isGlobalBan()));
    }


    @Override
    public PacketArguments getArguments() {
        return new PacketArguments()
                .setArgument("session_id", UUID.class)
                .setArgument("email", String.class)
                .setArgument("id", UUID.class)
                .setArgument("username", String.class)
                .setArgument("rating", Integer.class)
                .setArgument("level", Integer.class)
                .setArgument("xp", Integer.class)
                .setArgument("wins", Integer.class)
                .setArgument("bannedTime", Long.class)
                .setArgument("playtime", Long.class)
                .setArgument("competitiveBan", Boolean.class)
                .setArgument("onlineBan", Boolean.class)
                .setArgument("globalBan", Boolean.class);
    }
}
