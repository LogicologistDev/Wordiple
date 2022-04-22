package me.logicologist.wordiple.client.packets;

import com.olziedev.olziesocket.framework.PacketArguments;
import com.olziedev.olziesocket.framework.api.packet.PacketAdapter;
import com.olziedev.olziesocket.framework.api.packet.PacketType;
import me.logicologist.wordiple.client.manager.PacketManager;

import java.util.UUID;

public class LoginPacket extends PacketAdapter implements PacketType {

    public LoginPacket() {
        super("login_packet");
        this.packetType = this;
    }

    @Override
    public void onReceive(PacketArguments packetArguments) {

    }

    @Override
    public PacketArguments getArguments() {
        return new PacketArguments()
                .setArgument("username", String.class)
                .setArgument("password", String.class)
                .setArgument("response", UUID.class);
    }

    public void test() {
        PacketManager.getInstance().getSocket().getPacket(LoginPacket.class).sendPacket(packet -> packet.getPacketType().getArguments()
                .setValues("username", "test")
                .setValues("password", "test")
        );
    }
}
