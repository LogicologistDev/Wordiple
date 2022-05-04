package me.logicologist.wordiple.client.packets;

import com.olziedev.olziesocket.framework.PacketArguments;
import com.olziedev.olziesocket.framework.api.packet.PacketAdapter;
import com.olziedev.olziesocket.framework.api.packet.PacketType;

import java.util.UUID;

public class UserInfoPacket extends PacketAdapter implements PacketType {

    public UserInfoPacket() {
        super("user_info_packet");
        this.packetType = this;
    }

    @Override
    public boolean onlySendToServer() {
        return true;
    }

    @Override
    public void onReceive(PacketArguments packetArguments) {
        
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
