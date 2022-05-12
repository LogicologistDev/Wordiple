package me.logicologist.wordiple.server.packets.info;

import com.olziedev.olziesocket.framework.PacketArguments;
import com.olziedev.olziesocket.framework.api.packet.PacketAdapter;
import me.logicologist.wordiple.common.packets.AuthPacketType;
import me.logicologist.wordiple.common.queue.QueueType;
import me.logicologist.wordiple.server.managers.QueueManager;

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
    public void onReceive(PacketArguments arguments) {
        QueueType queueType = arguments.get("queuetype", QueueType.class);
        int active = QueueManager.getInstance().getQueue(queueType).getActive();
        this.sendPacket(packet -> arguments.replace(this.getArguments()).setValues("active", active));
    }

    @Override
    public PacketArguments getArguments() {
        return new PacketArguments()
                .setArgument("queuetype", QueueType.class)
                .setArgument("active", Integer.class);
    }
}
