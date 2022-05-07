package me.logicologist.wordiple.client.packets.auth;

import com.olziedev.olziesocket.framework.PacketArguments;
import com.olziedev.olziesocket.framework.api.packet.PacketType;

import java.util.UUID;

public interface AuthPacketType extends PacketType {

    default PacketArguments getArguments(UUID sessionID) {
        PacketArguments arguments = this.getArguments();
        if (arguments.getArgument("session") != null) arguments = arguments.setArgument("session", UUID.class);

        return arguments.setValues("session", sessionID);
    }
}
