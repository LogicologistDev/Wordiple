package me.logicologist.wordiple.client.packets.game;

import com.olziedev.olziesocket.framework.PacketArguments;
import com.olziedev.olziesocket.framework.api.packet.PacketAdapter;
import com.olziedev.olziesocket.framework.api.packet.PacketType;
import javafx.application.Platform;
import me.logicologist.wordiple.client.manager.GUIManager;

public class GameOverlayPacket extends PacketAdapter implements PacketType {


    public GameOverlayPacket() {
        super("game_overlay_packet");
        this.packetType = this;
    }

    @Override
    public boolean onlySendToServer() {
        return true;
    }

    @Override
    public void onReceive(PacketArguments packetArguments) {
        Platform.runLater(() -> {
            GUIManager.getInstance().showGameTextOverlay(packetArguments.get("display", String.class));
        });
    }

    @Override
    public PacketArguments getArguments() {
        return new PacketArguments()
                .setArgument("display", String.class);
    }
}
