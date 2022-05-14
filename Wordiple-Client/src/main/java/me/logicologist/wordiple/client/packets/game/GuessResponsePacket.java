package me.logicologist.wordiple.client.packets.game;

import com.olziedev.olziesocket.framework.PacketArguments;
import com.olziedev.olziesocket.framework.api.packet.PacketAdapter;
import javafx.application.Platform;
import me.logicologist.wordiple.client.manager.GUIManager;
import me.logicologist.wordiple.common.packets.AuthPacketType;
import me.logicologist.wordiple.common.queue.QueueType;

public class GuessResponsePacket extends PacketAdapter implements AuthPacketType {

    public GuessResponsePacket() {
        super("guess_response_packet");
        this.packetType = this;
    }

    @Override
    public boolean onlySendToServer() {
        return true;
    }

    @Override
    public void onReceive(PacketArguments packetArguments) {
        Platform.runLater(() -> {
            GUIManager.getInstance().getGameController().setPlayerGuessData(packetArguments.get("player", String.class), packetArguments.get("guess", Integer.class), packetArguments.get("data", String.class));
        });
    }

    @Override
    public PacketArguments getArguments() {
        return new PacketArguments()
                .setArgument("player", String.class)
                .setArgument("guess", String.class)
                .setArgument("data", Integer.class);
    }
}
