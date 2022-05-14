package me.logicologist.wordiple.server.packets.game;

import com.olziedev.olziesocket.framework.PacketArguments;
import com.olziedev.olziesocket.framework.api.packet.PacketAdapter;
import me.logicologist.wordiple.common.packets.AuthPacketType;
import me.logicologist.wordiple.server.managers.MatchManager;
import me.logicologist.wordiple.server.managers.SessionManager;
import me.logicologist.wordiple.server.user.WordipleUser;

public class GameReadyPacket extends PacketAdapter implements AuthPacketType {

    public GameReadyPacket() {
        super("game_ready_packet");
        this.packetType = this;
    }

    @Override
    public boolean onlySendToServer() {
        return true;
    }

    @Override
    public void onReceive(PacketArguments arguments) {
        // Packet used to unlock board (to client) and confirm ready to start game (to server)
        WordipleUser wordipleUser = SessionManager.getInstance().getSessionFromToken(this.getSessionID(arguments));
        if (wordipleUser == null) return;
        MatchManager.getInstance().getMatch(wordipleUser).readyClient(wordipleUser);
    }

    @Override
    public PacketArguments getArguments() {
        return new PacketArguments();
    }
}
