package me.logicologist.wordiple.server.packets.auth;

import com.olziedev.olziesocket.framework.PacketArguments;
import com.olziedev.olziesocket.framework.api.packet.PacketAdapter;
import com.olziedev.olziesocket.framework.api.packet.PacketType;
import me.logicologist.wordiple.server.managers.SessionManager;

import java.util.UUID;

public class LogoutPacket extends PacketAdapter implements PacketType {

    public LogoutPacket() {
        super("logout_packet");
        this.packetType = this;
    }

    @Override
    public boolean onlySendToServer() {
        return true;
    }

    @Override
    public void onReceive(PacketArguments packetArguments) {
        SessionManager.getInstance().removeSession(packetArguments.get("session", UUID.class));
    }

    @Override
    public PacketArguments getArguments() {
        return new PacketArguments().setArgument("session", UUID.class).setArgument("reason", String.class);
    }
}
