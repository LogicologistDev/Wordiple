package me.logicologist.wordiple.client.packets.game;

import com.olziedev.olziesocket.framework.PacketArguments;
import com.olziedev.olziesocket.framework.api.packet.PacketAdapter;
import me.logicologist.wordiple.common.packets.AuthPacketType;
import me.logicologist.wordiple.common.queue.QueueType;

public class GuessResponsePacket extends PacketAdapter implements AuthPacketType {

    public GuessResponsePacket() {
        super("guess_response_packet");
        this.packetType = this;
    }

    @Override
    public void onReceive(PacketArguments packetArguments) {

    }

    @Override
    public PacketArguments getArguments() {
        return new PacketArguments()
                .setArgument("player", QueueType.class)
                .setArgument("guess", String.class)
                .setArgument("data", Integer.class);
    }
}
