package me.logicologist.wordiple.client.packets.info;

import com.olziedev.olziesocket.framework.PacketArguments;
import com.olziedev.olziesocket.framework.api.packet.PacketAdapter;
import me.logicologist.wordiple.common.packets.AuthPacketType;

public class QueueInfoPacket extends PacketAdapter implements AuthPacketType {

    public QueueInfoPacket() {
        super("queue_info_packet");
    }

    @Override
    public boolean onlySendToServer() {
        return true;
    }

    @Override
    public void onReceive(PacketArguments packetArguments) {

    }

    @Override
    public PacketArguments getArguments() {
        return new PacketArguments()
                .setArgument("queuetype", String.class)
                .setArgument("active", Integer.class);
    }
}
