package me.logicologist.wordiple.client.manager;

import com.olziedev.olziesocket.OlzieSocket;
import com.olziedev.olziesocket.framework.SocketConfig;
import com.olziedev.olziesocket.framework.action.SocketActionType;
import me.logicologist.wordiple.client.WordipleClient;
import me.logicologist.wordiple.client.gui.controllers.LoadScreenController;
import me.logicologist.wordiple.client.packets.info.UserInfoPacket;
import me.logicologist.wordiple.common.packets.AuthPacketType;
import me.logicologist.wordiple.common.utils.Utils;
import org.apache.logging.log4j.LogManager;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class PacketManager {

    private static PacketManager instance;
    private final OlzieSocket socket;

    public PacketManager(boolean developerMode) {
        SocketConfig config = new SocketConfig("157.90.218.221", 11184, "", developerMode, new SocketConfig.SocketHeartbeatConfig(2000, 20), (short) 0);
        this.socket = new OlzieSocket(getClass(), config, LogManager.getLogger("Wordiple-Server-Packet"));
        instance = this;
    }

    public void load() {
        SessionManager manager = SessionManager.getInstance();
        this.socket.registerPackets();
        this.socket.connect(true, socket -> {
            if (GUIManager.getInstance() != null) return; // game already launched.

            UUID id = manager.getLocalSessionID();
            if (id == null) return;

            this.socket.getPacket(UserInfoPacket.class)
                    .sendPacket(packet -> packet.getPacketType(AuthPacketType.class).getArguments(id))
                    .waitForResponse(response -> {
                        String username = response.get("username", String.class);
                        if (username == null) {
                            GUIManager.addReadyListener(instance -> instance.showLoginScreen(true));
                            return false;
                        }
                        manager.load(response, username);
                        GUIManager.addReadyListener(instance -> {
                            if (!Utils.getVersion().equals(manager.getVersion())) {
                                instance.showLoginScreen(true);
                                return;
                            }
                            instance.startSwipeTransition(null, () -> {
                                GUIManager.getInstance().showGameSelectScreen(false);
                            });
                        });
                        return false;
                    }, null, 5, TimeUnit.SECONDS);
        });
        this.socket.getActionRegister().registerAction(SocketActionType.CONNECTION_LOST, (action) -> {
            if (!manager.isLoggedIn()) return;

            SoundManager.getInstance().stopSounds();
            GUIManager.addReadyListener(instance -> {
                instance.startSwipeTransition(null, () -> {
                    instance.showMainScreen(false);
                    LoadScreenController lsc = instance.showLoadScreen("Connection has been lost!");
                    manager.setLocalSessionID(null, false);
                    WordipleClient.getExecutor().schedule(() -> lsc.remove(null), 2, TimeUnit.SECONDS);
                });
            });
        });
    }

    public static PacketManager getInstance() {
        return instance;
    }

    public OlzieSocket getSocket() {
        return socket;
    }
}
