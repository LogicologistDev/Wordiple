package me.logicologist.wordiple.client.packets.game;

import com.olziedev.olziesocket.framework.PacketArguments;
import com.olziedev.olziesocket.framework.api.packet.PacketAdapter;
import me.logicologist.wordiple.common.packets.AuthPacketType;
import me.logicologist.wordiple.common.queue.QueueType;

public class GameStartPacket extends PacketAdapter implements AuthPacketType {

    public GameStartPacket() {
        super("game_start_packet");
        this.packetType = this;
    }

    @Override
    public void onReceive(PacketArguments packetArguments) {

    }

    @Override
    public PacketArguments getArguments() {
        return new PacketArguments()
                .setArgument("type", QueueType.class)
                .setArgument("opponent", String.class)
                .setArgument("rating", Integer.class)
                .setArgument("goal", Integer.class);
    }
}
