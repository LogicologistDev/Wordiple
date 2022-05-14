package me.logicologist.wordiple.client.packets.game;

import com.olziedev.olziesocket.framework.PacketArguments;
import com.olziedev.olziesocket.framework.api.packet.PacketAdapter;
import javafx.application.Platform;
import me.logicologist.wordiple.client.manager.GUIManager;
import me.logicologist.wordiple.common.packets.AuthPacketType;

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
        Platform.runLater(() -> {
            GUIManager.getInstance().getGameController().setOpponentDisplay(arguments.get("name", String.class), arguments.get("guess", Integer.class), arguments.get("length", Integer.class));
        });
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
