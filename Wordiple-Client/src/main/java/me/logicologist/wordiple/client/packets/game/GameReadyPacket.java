package me.logicologist.wordiple.client.packets.game;

import com.olziedev.olziesocket.framework.PacketArguments;
import com.olziedev.olziesocket.framework.api.packet.PacketAdapter;
import javafx.application.Platform;
import javafx.scene.layout.AnchorPane;
import me.logicologist.wordiple.client.gui.controllers.game.GameController;
import me.logicologist.wordiple.client.manager.GUIManager;
import me.logicologist.wordiple.client.manager.SessionManager;
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
        Platform.runLater(() -> {
            GameController gameController = GUIManager.getInstance().getGameController();
            gameController.setAnswerLocked(false);
            for (AnchorPane pane : gameController.playerPanes.values()) {
                gameController.setRowData(gameController.getPlayerPanes(pane).get(0), "rrrrr");
            }
        });
    }

    @Override
    public PacketArguments getArguments() {
        return new PacketArguments();
    }
}
