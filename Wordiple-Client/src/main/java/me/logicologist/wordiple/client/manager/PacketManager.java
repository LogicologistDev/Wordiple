package me.logicologist.wordiple.client.manager;

import com.olziedev.olziesocket.OlzieSocket;
import com.olziedev.olziesocket.framework.SocketConfig;
import com.olziedev.olziesocket.framework.action.SocketActionType;
import javafx.application.Platform;
import me.logicologist.wordiple.client.packets.UserInfoPacket;
import org.apache.logging.log4j.LogManager;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class PacketManager {

    private static PacketManager instance;
    private final OlzieSocket socket;

    public PacketManager() {
        SocketConfig config = new SocketConfig("157.90.218.221", 11184, "", false, new SocketConfig.SocketHeartbeatConfig(2000, 20)).setRestrictPacketSending(true);
        this.socket = new OlzieSocket(getClass(), config, LogManager.getLogger("Wordiple-Server-Packet"));
        instance = this;
    }

    public void load() {
        this.socket.registerPackets();
        this.socket.connect(false, socket -> {
            UUID id = SessionManager.getInstance().getLocalSessionID();
            this.socket.getPacket(UserInfoPacket.class)
                    .sendPacket(packet -> packet.getPacketType().getArguments().setValues("session_id", id))
                    .waitForResponse(response -> {
                        String username = response.get("username", String.class);
                        if (username == null) {
                            return false;
                        }
                        Platform.runLater(() -> GUIManager.getInstance().startSwipeTransition(null, () -> GUIManager.getInstance().showGameSelectScreen(false)));
                        return false;
                    }, null, 5, TimeUnit.SECONDS);
        });
        this.socket.getActionRegister().registerAction(SocketActionType.CONNECTION_LOST, (action) -> {

        });
    }

    public static PacketManager getInstance() {
        return instance;
    }

    public OlzieSocket getSocket() {
        return socket;
    }
}
