package me.logicologist.wordiple.server.packets.info;

import com.olziedev.olziesocket.framework.PacketArguments;
import com.olziedev.olziesocket.framework.api.packet.PacketAdapter;
import me.logicologist.wordiple.common.packets.AuthPacketType;
import me.logicologist.wordiple.common.queue.QueueType;

public class QueueInfoPacket extends PacketAdapter implements AuthPacketType {

    public QueueInfoPacket() {
        super("queue_info_packet");
        this.packetType = this;
    }

    @Override
    public boolean onlySendToServer() {
        return true;
    }

    @Override
    public void onReceive(PacketArguments packetArguments) {
        this.sendPacket(packet -> packetArguments.replace(this.getArguments()).setValues("active", 0));
    }

    @Override
    public PacketArguments getArguments() {
        return new PacketArguments()
                .setArgument("queuetype", QueueType.class)
                .setArgument("active", Integer.class);
    }
}
