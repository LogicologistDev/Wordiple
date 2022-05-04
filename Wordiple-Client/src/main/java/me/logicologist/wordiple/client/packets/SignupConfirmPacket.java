package me.logicologist.wordiple.client.packets;

import com.olziedev.olziesocket.framework.PacketArguments;
import com.olziedev.olziesocket.framework.api.packet.PacketAdapter;
import com.olziedev.olziesocket.framework.api.packet.PacketType;

import java.util.UUID;

public class SignupConfirmPacket extends PacketAdapter implements PacketType {

    public SignupConfirmPacket() {
        super("signup_confirm_packet");
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
                .setArgument("code", String.class)
                .setArgument("email", String.class)
                .setArgument("response", UUID.class);
    }
}