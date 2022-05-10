package me.logicologist.wordiple.client.packets.info;

import com.olziedev.olziesocket.framework.PacketArguments;
import com.olziedev.olziesocket.framework.api.packet.PacketAdapter;
import me.logicologist.wordiple.client.manager.GUIManager;
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
    public void onReceive(PacketArguments arguments) {
        GUIManager.getInstance().getQueueController().setActive(arguments.get("active", Integer.class));
    }

    @Override
    public PacketArguments getArguments() {
        return new PacketArguments()
                .setArgument("queuetype", QueueType.class)
                .setArgument("active", Integer.class);
    }
}
