package me.logicologist.wordiple.server.packets.auth;

import com.olziedev.olziesocket.framework.PacketArguments;
import com.olziedev.olziesocket.framework.api.packet.PacketAdapter;
import com.olziedev.olziesocket.framework.api.packet.PacketType;
import me.logicologist.wordiple.common.packets.AuthPacketType;
import me.logicologist.wordiple.server.managers.SessionManager;

import java.util.UUID;

public class LogoutPacket extends PacketAdapter implements AuthPacketType {

    public LogoutPacket() {
        super("logout_packet");
        this.packetType = this;
    }

    @Override
    public boolean onlySendToServer() {
        return true;
    }

    @Override
    public void onReceive(PacketArguments arguments) {
        SessionManager.getInstance().removeSession(this.getSessionID(arguments));
    }

    @Override
    public PacketArguments getArguments() {
        return new PacketArguments().setArgument("reason", String.class);
    }
}
