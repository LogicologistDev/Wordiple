package me.logicologist.wordiple.client.packets.game;

import com.olziedev.olziesocket.framework.PacketArguments;
import com.olziedev.olziesocket.framework.api.packet.PacketAdapter;
import com.olziedev.olziesocket.framework.api.packet.PacketType;
import javafx.application.Platform;
import me.logicologist.wordiple.client.manager.GUIManager;

public class ResetBoardsPacket extends PacketAdapter implements PacketType {

    public ResetBoardsPacket() {
        super("reset_boards_packet");
    }

    @Override
    public boolean onlySendToServer() {
        return true;
    }

    @Override
    public void onReceive(PacketArguments packetArguments) {
        Platform.runLater(() -> {
            GUIManager.getInstance().getGameController().resetBoards();
        });
    }

    @Override
    public PacketArguments getArguments() {
        return new PacketArguments();
    }
}
