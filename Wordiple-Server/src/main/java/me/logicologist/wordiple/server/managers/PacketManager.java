package me.logicologist.wordiple.server.managers;

import com.olziedev.olziesocket.OlzieSocket;
import com.olziedev.olziesocket.framework.SocketConfig;
import org.apache.logging.log4j.LogManager;

public class PacketManager {

    private static PacketManager instance;
    private final OlzieSocket socket;

    public PacketManager() {
        SocketConfig config = new SocketConfig("127.0.0.1", 11184, "", false, new SocketConfig.SocketHeartbeatConfig(2000, 20), (short) 0);
//        this.socket = new OlzieSocket(getClass(), config, LogManager.getLogger("Wordiple-Server-Packet"));
        this.socket = new OlzieSocket(getClass(), config, null);
        instance = this;
    }

    public void load() {
        this.socket.registerPackets();
        this.socket.setupServer();
    }

    public static PacketManager getInstance() {
        return instance;
    }

    public OlzieSocket getSocket() {
        return socket;
    }
}
