package me.logicologist.wordiple.client.packets.auth;

import com.olziedev.olziesocket.framework.PacketArguments;
import com.olziedev.olziesocket.framework.api.packet.PacketAdapter;
import com.olziedev.olziesocket.framework.api.packet.PacketType;

public class ResetUrPasswordPacket extends PacketAdapter implements PacketType {

    public ResetUrPasswordPacket() {
        super("reset_ur_password_packet");
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
                .setArgument("email", String.class)
                .setArgument("password", String.class);
    }
}
