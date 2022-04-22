package me.logicologist.wordiple.server.packets;

import com.olziedev.olziesocket.framework.PacketArguments;
import com.olziedev.olziesocket.framework.api.packet.PacketAdapter;
import com.olziedev.olziesocket.framework.api.packet.PacketType;

import java.util.UUID;

public class SignupPacket extends PacketAdapter implements PacketType {

    public SignupPacket() {
        super("signup_packet");
        this.packetType = this;
    }

    @Override
    public void onReceive(PacketArguments packetArguments) {
        String email = packetArguments.get("email", String.class);
        String username = packetArguments.get("username", String.class);
        String password = packetArguments.get("password", String.class);
    }

    @Override
    public PacketArguments getArguments() {
        return new PacketArguments()
                .setArgument("email", String.class)
                .setArgument("username", String.class)
                .setArgument("password", String.class)
                .setArgument("response", UUID.class);
    }
}
