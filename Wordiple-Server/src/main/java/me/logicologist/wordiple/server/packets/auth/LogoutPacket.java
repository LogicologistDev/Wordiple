package me.logicologist.wordiple.server.packets.auth;

import com.olziedev.olziesocket.framework.PacketArguments;
import com.olziedev.olziesocket.framework.api.packet.PacketAdapter;
import me.logicologist.wordiple.common.packets.AuthPacketType;
import me.logicologist.wordiple.server.WordipleServer;
import me.logicologist.wordiple.server.managers.DatabaseManager;
import me.logicologist.wordiple.server.managers.QueueManager;
import me.logicologist.wordiple.server.managers.SessionManager;
import me.logicologist.wordiple.server.user.WordipleUser;

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
        WordipleServer.getLogger().info("Logging out and saving user data?");
        if (wordipleUser == null) return; // WHAT?

        WordipleServer.getLogger().info("Logging out and saving user data: " + wordipleUser.getEmail());
        QueueManager.getInstance().removeFromAllQueues(wordipleUser);
        QueueManager.getInstance().removeAllQueueViewers(wordipleUser);
        DatabaseManager.instance.saveUser(wordipleUser);
        wordipleUser.setSocket(null);
    }

    @Override
    public PacketArguments getArguments() {
        return new PacketArguments().setArgument("logout", Boolean.class).setArgument("reason", String.class);
    }
}
