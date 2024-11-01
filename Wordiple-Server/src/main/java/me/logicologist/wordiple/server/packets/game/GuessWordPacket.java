package me.logicologist.wordiple.server.packets.game;

import com.olziedev.olziesocket.framework.PacketArguments;
import com.olziedev.olziesocket.framework.api.packet.PacketAdapter;
import me.logicologist.wordiple.common.packets.AuthPacketType;
import me.logicologist.wordiple.server.managers.MatchManager;
import me.logicologist.wordiple.server.managers.SessionManager;
import me.logicologist.wordiple.server.user.WordipleUser;

public class GuessWordPacket extends PacketAdapter implements AuthPacketType {

    public GuessWordPacket() {
        super("guess_word_packet");
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
        MatchManager.getInstance().getMatch(wordipleUser).getCurrentRound().addGuess(wordipleUser, arguments.get("word", String.class));
    }

    @Override
    public PacketArguments getArguments() {
        return new PacketArguments()
                .setArgument("word", String.class);
    }
}
