package me.logicologist.wordiple.client.packets.game;

import com.olziedev.olziesocket.framework.PacketArguments;
import com.olziedev.olziesocket.framework.api.packet.PacketAdapter;
import me.logicologist.wordiple.client.manager.GUIManager;
import me.logicologist.wordiple.common.packets.AuthPacketType;

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
        GUIManager.getInstance().getGameController().setAnswerLocked(false);
    }

    @Override
    public PacketArguments getArguments() {
        return new PacketArguments();
    }
}
