package me.logicologist.wordiple.client.packets.auth;

import com.olziedev.olziesocket.framework.PacketArguments;
import com.olziedev.olziesocket.framework.api.packet.PacketAdapter;
import javafx.application.Platform;
import me.logicologist.wordiple.client.WordipleClient;
import me.logicologist.wordiple.client.gui.controllers.LoadScreenController;
import me.logicologist.wordiple.client.manager.GUIManager;
import me.logicologist.wordiple.client.manager.SessionManager;
import me.logicologist.wordiple.common.packets.AuthPacketType;

import java.util.concurrent.TimeUnit;

public class LogoutPacket extends PacketAdapter implements AuthPacketType {

    public LogoutPacket() {
        super("logout_packet");
        this.packetType = this;
    }

    @Override
    public boolean onlySendToServer() {
        return true;
    }

    @Override
    public void onReceive(PacketArguments packetArguments) {
        GUIManager guiManager = GUIManager.getInstance();
        Platform.runLater(() -> GUIManager.getInstance().startSwipeTransition(null, () -> {
            guiManager.showMainScreen(false);
            LoadScreenController lsc = guiManager.showLoadScreen(packetArguments.get("reason", String.class));
            SessionManager.getInstance().setLocalSessionID(null);
            WordipleClient.getExecutor().schedule(() -> lsc.remove(null), 2, TimeUnit.SECONDS);
        }));
    }

    @Override
    public PacketArguments getArguments() {
        return new PacketArguments().setArgument("logout", Boolean.class).setArgument("reason", String.class);
    }
}
