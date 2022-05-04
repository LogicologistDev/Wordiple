package me.logicologist.wordiple.server.packets;

import com.olziedev.olziesocket.framework.PacketArguments;
import com.olziedev.olziesocket.framework.api.packet.PacketAdapter;
import com.olziedev.olziesocket.framework.api.packet.PacketType;
import me.logicologist.wordiple.server.managers.SessionManager;

import java.util.UUID;

public class LoginPacket extends PacketAdapter implements PacketType {

    public LoginPacket() {
        super("login_packet");
        this.packetType = this;
    }

    @Override
    public void onReceive(PacketArguments packetArguments) {
        String username = packetArguments.get("username", String.class);
        String password = packetArguments.get("password", String.class);
        UUID session = SessionManager.getInstance().createSession(username, password);
        this.sendPacket(packet -> packetArguments.replace(this.getArguments()).setValues("response", session));
    }

    @Override
    public PacketArguments getArguments() {
        return new PacketArguments()
                .setArgument("username", String.class)
                .setArgument("password", String.class)
                .setArgument("response", UUID.class);
    }
}
