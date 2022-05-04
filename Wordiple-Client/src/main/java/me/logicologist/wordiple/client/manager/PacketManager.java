package me.logicologist.wordiple.client.manager;

import com.olziedev.olziesocket.OlzieSocket;
import com.olziedev.olziesocket.framework.SocketConfig;
import com.olziedev.olziesocket.framework.action.SocketActionType;
import org.apache.logging.log4j.LogManager;

public class PacketManager {

    private static PacketManager instance;
    private final OlzieSocket socket;

    public PacketManager() {
        SocketConfig config = new SocketConfig("157.90.218.221", 11184, "", true/*, new SocketConfig.SocketHeartbeatConfig(2000, 20)*/);
        this.socket = new OlzieSocket(getClass(), config, LogManager.getLogger("Wordiple-Server-Packet"));
        instance = this;
    }

    public void load() {
        this.socket.registerPackets();
        this.socket.connect(false);
        this.socket.getActionRegister().registerAction(SocketActionType.CONNECTION_LOST, (action) -> null);
    }

    public static PacketManager getInstance() {
        return instance;
    }

    public OlzieSocket getSocket() {
        return socket;
    }
}
