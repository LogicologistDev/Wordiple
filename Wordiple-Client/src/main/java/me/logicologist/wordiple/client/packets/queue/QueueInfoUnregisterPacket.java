package me.logicologist.wordiple.client.packets.queue;

import com.olziedev.olziesocket.framework.PacketArguments;
import com.olziedev.olziesocket.framework.api.packet.PacketAdapter;
import me.logicologist.wordiple.common.packets.AuthPacketType;
import me.logicologist.wordiple.common.queue.QueueType;

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

    }

    @Override
    public PacketArguments getArguments() {
        return new PacketArguments()
                .setArgument("queuetype", QueueType.class);
    }

    // Packet for removing the session ID from the list of people getting updates on a certain queue.
}
