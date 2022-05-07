package me.logicologist.wordiple.server.packets.auth;

import com.olziedev.olziesocket.framework.PacketArguments;
import com.olziedev.olziesocket.framework.api.packet.PacketAdapter;
import com.olziedev.olziesocket.framework.api.packet.PacketType;

public class ForgotUrPasswordPacket extends PacketAdapter implements PacketType {

    public ForgotUrPasswordPacket() {
        super("forgot_ur_password_packet");
        this.packetType = new AuthPacket();
    }

    @Override
    public void onReceive(PacketArguments packetArguments) {

    }

    @Override
    public PacketArguments getArguments() {
        return new PacketArguments(this.packetType.getArguments());
    }
}
