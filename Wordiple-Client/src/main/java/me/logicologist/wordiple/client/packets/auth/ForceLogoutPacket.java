package me.logicologist.wordiple.client.packets.auth;

import com.olziedev.olziesocket.framework.PacketArguments;
import com.olziedev.olziesocket.framework.api.packet.PacketAdapter;
import com.olziedev.olziesocket.framework.api.packet.PacketType;
import javafx.application.Platform;
import me.logicologist.wordiple.client.WordipleClient;
import me.logicologist.wordiple.client.gui.controllers.LoadScreenController;
import me.logicologist.wordiple.client.manager.GUIManager;
import me.logicologist.wordiple.client.manager.SessionManager;

import java.util.concurrent.TimeUnit;

public class ForceLogoutPacket extends PacketAdapter implements PacketType {

    public ForceLogoutPacket() {
        super("force_logout_packet");
        this.packetType = this;
    }

    @Override
    public void onReceive(PacketArguments packetArguments) {
        GUIManager guiManager = GUIManager.getInstance();
        Platform.runLater(() -> {
            GUIManager.getInstance().startSwipeTransition(null, () -> {
                guiManager.showMainScreen(false);
                LoadScreenController lsc = guiManager.showLoadScreen("Connection has been lost!");
                SessionManager.getInstance().setLocalSessionID(null);
                WordipleClient.getExecutor().schedule(() -> lsc.remove(null), 2, TimeUnit.SECONDS);
            });
        });
    }

    @Override
    public PacketArguments getArguments() {
        return new PacketArguments().setArgument("reason", String.class);
    }
}
