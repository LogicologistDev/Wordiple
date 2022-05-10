package me.logicologist.wordiple.server.packets.auth;

import com.olziedev.olziesocket.framework.PacketArguments;
import com.olziedev.olziesocket.framework.api.packet.PacketAdapter;
import com.olziedev.olziesocket.framework.api.packet.PacketType;
import me.logicologist.wordiple.common.packets.AuthPacketType;
import me.logicologist.wordiple.server.WordipleServer;
import me.logicologist.wordiple.server.managers.DatabaseManager;
import me.logicologist.wordiple.server.managers.QueueManager;
import me.logicologist.wordiple.server.managers.SessionManager;
import me.logicologist.wordiple.server.user.WordipleUser;

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
        SessionManager manager = SessionManager.getInstance();
        WordipleUser wordipleUser = manager.getSessionFromToken(this.getSessionID(arguments));
        if (arguments.get("logout", Boolean.class)) {
            WordipleServer.getLogger().info("Logging out user.");
            SessionManager.getInstance().removeSession(this.getSessionID(arguments));
        }
        if (wordipleUser == null) return; // WHAT?
        QueueManager.getInstance().removeFromAllQueues(wordipleUser);
        QueueManager.getInstance().removeAllQueueViewers(wordipleUser);
        DatabaseManager.instance.saveUser(wordipleUser);
    }

    @Override
    public PacketArguments getArguments() {
        return new PacketArguments().setArgument("logout", Boolean.class).setArgument("reason", String.class);
    }
}
