package me.logicologist.wordiple.server.packets.game;

import com.olziedev.olziesocket.framework.PacketArguments;
import com.olziedev.olziesocket.framework.api.packet.PacketAdapter;
import me.logicologist.wordiple.common.packets.AuthPacketType;
import me.logicologist.wordiple.server.managers.MatchManager;
import me.logicologist.wordiple.server.managers.SessionManager;
import me.logicologist.wordiple.server.match.round.Round;
import me.logicologist.wordiple.server.user.WordipleUser;

public class UpdateDisplayPacket extends PacketAdapter implements AuthPacketType {

    public UpdateDisplayPacket() {
        super("update_display_packet");
        this.packetType = this;
    }

    @Override
    public boolean onlySendToServer() {
        return true;
    }

    @Override
    public void onReceive(PacketArguments arguments) {
        WordipleUser wordipleUser = SessionManager.getInstance().getSessionFromToken(this.getSessionID(arguments));
        if (wordipleUser == null) return;
        Round round = MatchManager.getInstance().getMatch(wordipleUser).getCurrentRound();
        round.setDisplayText(arguments.get("name", String.class), arguments.get("text", String.class));
    }

    @Override
    public PacketArguments getArguments() {
        return new PacketArguments()
                .setArgument("name", String.class)
                .setArgument("length", Integer.class)
                .setArgument("guess", Integer.class)
                .setArgument("text", String.class);
    }
}
