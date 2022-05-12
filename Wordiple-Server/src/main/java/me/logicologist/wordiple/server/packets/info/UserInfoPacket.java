package me.logicologist.wordiple.server.packets.info;

import com.olziedev.olziesocket.framework.PacketArguments;
import com.olziedev.olziesocket.framework.api.packet.PacketAdapter;
import me.logicologist.wordiple.common.packets.AuthPacketType;
import me.logicologist.wordiple.common.utils.Utils;
import me.logicologist.wordiple.server.managers.PacketManager;
import me.logicologist.wordiple.server.managers.SessionManager;
import me.logicologist.wordiple.server.packets.auth.LogoutPacket;
import me.logicologist.wordiple.server.user.WordipleUser;

import java.io.ObjectOutputStream;
import java.util.Date;
import java.util.UUID;

public class UserInfoPacket extends PacketAdapter implements AuthPacketType {

    public UserInfoPacket() {
        super("user_info_packet");
        this.packetType = this;
    }

    @Override
    public boolean onlySendToServer() {
        return true;
    }

    @Override
    public void onReceive(PacketArguments arguments) {
        SessionManager sessionManager = SessionManager.getInstance();
        WordipleUser wordipleUser = sessionManager.getSessionFromToken(this.getSessionID(arguments));
        if (wordipleUser == null) {
            this.sendPacket(packet -> arguments.replace(this.getArguments()));
            return;
        }
        ObjectOutputStream oldStream = wordipleUser.getOutputStream();
        if (!PacketManager.getInstance().getSocket().getOutputStream(arguments.getPacketHolder()).equals(oldStream)) {
            PacketManager.getInstance().getSocket().getPacket(LogoutPacket.class)
                    .sendPacket(packet -> packet.getPacketType().getArguments().setValues("reason", "You have been logged out."), oldStream);
        }
        wordipleUser.setSocket(arguments.getPacketHolder());
        wordipleUser.setLoggedInTime(new Date());
        this.sendPacket(packet -> arguments.replace(this.getArguments())
                .setValues("email", wordipleUser.getEmail())
                .setValues("id", wordipleUser.getId())
                .setValues("username", wordipleUser.getUsername())
                .setValues("rating", wordipleUser.getRating())
                .setValues("level", wordipleUser.getLevel())
                .setValues("xp", wordipleUser.getExperience())
                .setValues("neededXp", wordipleUser.getNeededExperience())
                .setValues("wins", wordipleUser.getWins())
                .setValues("bannedTime", wordipleUser.getBannedTime())
                .setValues("playtime", wordipleUser.getPlaytime())
                .setValues("competitiveBan", wordipleUser.isCompetitiveBan())
                .setValues("onlineBan", wordipleUser.isOnlineBan())
                .setValues("version", Utils.getVersion())
                .setValues("globalBan", wordipleUser.isGlobalBan()));
    }


    @Override
    public PacketArguments getArguments() {
        return new PacketArguments()
                .setArgument("email", String.class)
                .setArgument("id", UUID.class)
                .setArgument("username", String.class)
                .setArgument("rating", Integer.class)
                .setArgument("level", Integer.class)
                .setArgument("xp", Integer.class)
                .setArgument("neededXp", Integer.class)
                .setArgument("wins", Integer.class)
                .setArgument("bannedTime", Long.class)
                .setArgument("playtime", Long.class)
                .setArgument("competitiveBan", Boolean.class)
                .setArgument("onlineBan", Boolean.class)
                .setArgument("version", String.class)
                .setArgument("globalBan", Boolean.class);
    }
}
