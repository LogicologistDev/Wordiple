package me.logicologist.wordiple.client.packets.game;

import com.olziedev.olziesocket.framework.PacketArguments;
import com.olziedev.olziesocket.framework.api.packet.PacketAdapter;
import com.olziedev.olziesocket.framework.api.packet.PacketType;
import javafx.application.Platform;
import me.logicologist.wordiple.client.manager.GUIManager;
import me.logicologist.wordiple.client.manager.SoundManager;
import me.logicologist.wordiple.client.sound.SoundType;

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
            Integer countdownPosition = packetArguments.get("countdown_position", Integer.class);
            if (countdownPosition == null) return;

            switch (countdownPosition) {
                case 1:
                case 2:
                    SoundManager.getInstance().playSound(SoundType.COUNTDOWN_STAGE_2);
                    break;
                case 3:
                    SoundManager.getInstance().playSound(SoundType.COUNTDOWN_STAGE_1);
                    break;
            }
        });
    }

    @Override
    public PacketArguments getArguments() {
        return new PacketArguments()
                .setArgument("display", String.class)
                .setArgument("countdown_position", Integer.class);
    }
}
