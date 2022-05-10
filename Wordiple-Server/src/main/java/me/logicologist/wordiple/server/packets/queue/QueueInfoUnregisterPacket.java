package me.logicologist.wordiple.server.packets.queue;

import com.olziedev.olziesocket.framework.PacketArguments;
import com.olziedev.olziesocket.framework.api.packet.PacketAdapter;
import me.logicologist.wordiple.common.packets.AuthPacketType;
import me.logicologist.wordiple.common.queue.QueueType;
import me.logicologist.wordiple.server.managers.QueueManager;
import me.logicologist.wordiple.server.managers.SessionManager;
import me.logicologist.wordiple.server.user.WordipleUser;

public class QueueInfoUnregisterPacket extends PacketAdapter implements AuthPacketType {

    public QueueInfoUnregisterPacket() {
        super("queue_info_unregister_packet");
        this.packetType = this;
    }

    @Override
    public boolean onlySendToServer() {
        return true;
    }

    @Override
    public void onReceive(PacketArguments arguments) {
        QueueType queueType = arguments.get("queuetype", QueueType.class);
        WordipleUser wordipleUser = SessionManager.getInstance().getSessionFromToken(this.getSessionID(arguments));
        QueueManager.getInstance().getQueue(queueType).removeQueueViewer(wordipleUser);
        this.sendPacket(packet -> arguments.replace(this.getArguments()));
    }

    @Override
    public PacketArguments getArguments() {
        return new PacketArguments()
                .setArgument("queuetype", QueueType.class);
    }

    // Packet for removing the session ID from the list of people getting updates on a certain queue.
}
