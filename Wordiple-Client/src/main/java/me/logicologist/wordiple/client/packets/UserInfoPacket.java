package me.logicologist.wordiple.client.packets;

import com.olziedev.olziesocket.framework.PacketArguments;
import com.olziedev.olziesocket.framework.api.packet.PacketAdapter;
import me.logicologist.wordiple.common.packets.AuthPacketType;

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
                .setArgument("globalBan", Boolean.class);
    }
}
