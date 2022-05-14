package me.logicologist.wordiple.client.packets.game;

import com.olziedev.olziesocket.framework.PacketArguments;
import com.olziedev.olziesocket.framework.api.packet.PacketAdapter;
import javafx.application.Platform;
import me.logicologist.wordiple.client.manager.GUIManager;
import me.logicologist.wordiple.client.manager.SessionManager;
import me.logicologist.wordiple.common.packets.AuthPacketType;
import me.logicologist.wordiple.common.queue.QueueType;

public class GameStartPacket extends PacketAdapter implements AuthPacketType {

    public GameStartPacket() {
        super("game_start_packet");
        this.packetType = this;
    }

    @Override
    public boolean onlySendToServer() {
        return true;
    }

    @Override
    public void onReceive(PacketArguments packetArguments) {
        switch (packetArguments.get("type", QueueType.class)) {
            case COMPETITIVE:
                Platform.runLater(() -> GUIManager.getInstance().showCompetitiveIntro(null, () -> {
                    GUIManager.getInstance().showVersusTwoBoard(packetArguments);
                }, () -> olzieSocket.getPacket(GameReadyPacket.class).sendPacket(packet -> packet.getPacketType(AuthPacketType.class).getArguments(SessionManager.getInstance().getLocalSessionID())), packetArguments.get("opponent", String.class), packetArguments.get("rating", Integer.class)));
                break;
        }
        return;

    }

    @Override
    public PacketArguments getArguments() {
        return new PacketArguments()
                .setArgument("type", QueueType.class)
                .setArgument("opponent", String.class)
                .setArgument("rating", Integer.class)
                .setArgument("goal", String.class);
    }
}
