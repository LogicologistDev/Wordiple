package me.logicologist.wordiple.server.packets.auth;

import com.olziedev.olziesocket.framework.PacketArguments;
import com.olziedev.olziesocket.framework.api.packet.PacketAdapter;
import com.olziedev.olziesocket.framework.api.packet.PacketType;
import me.logicologist.wordiple.server.managers.SessionManager;

import java.util.UUID;

public class LoginPacket extends PacketAdapter implements PacketType {

    @Override
    public boolean onlySendToServer() {
        return true;
    }

    public LoginPacket() {
        super("login_packet");
        this.packetType = this;
    }

    @Override
    public void onReceive(PacketArguments packetArguments) {
        String username = packetArguments.get("username", String.class);
        String passwordHash = packetArguments.get("password_hash", String.class);
        UUID session = SessionManager.getInstance().createSession(username, passwordHash, packetArguments.getPacketHolder());
        this.sendPacket(packet -> packetArguments.replace(this.getArguments()).setValues("response", session));
    }

    @Override
    public PacketArguments getArguments() {
        return new PacketArguments()
                .setArgument("username", String.class)
                .setArgument("password_hash", String.class)
                .setArgument("response", UUID.class);
    }
}
