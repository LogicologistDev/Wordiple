package me.logicologist.wordiple.client.packets.game;

import com.olziedev.olziesocket.framework.PacketArguments;
import com.olziedev.olziesocket.framework.api.packet.PacketAdapter;
import com.olziedev.olziesocket.framework.api.packet.PacketType;
import javafx.application.Platform;
import me.logicologist.wordiple.client.manager.GUIManager;

public class UpdateScoreboardPacket extends PacketAdapter implements PacketType {

    public UpdateScoreboardPacket() {
        super("update_scoreboard_packet");
    }

    @Override
    public boolean onlySendToServer() {
        return true;
    }

    @Override
    public void onReceive(PacketArguments arguments) {
        Platform.runLater(() -> {
            GUIManager.getInstance().getGameController().setScore(arguments.get("player", String.class), arguments.get("score", Integer.class));
        });
    }

    @Override
    public PacketArguments getArguments() {
        return new PacketArguments()
                .setArgument("player", String.class)
                .setArgument("score", Integer.class);
    }


}
