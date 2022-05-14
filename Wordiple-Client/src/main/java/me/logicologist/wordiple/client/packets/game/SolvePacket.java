package me.logicologist.wordiple.client.packets.game;

import com.olziedev.olziesocket.framework.PacketArguments;
import com.olziedev.olziesocket.framework.api.packet.PacketAdapter;
import me.logicologist.wordiple.common.packets.AuthPacketType;

public class SolvePacket extends PacketAdapter implements AuthPacketType {

    public SolvePacket() {
        super("solve_packet");
        this.packetType = this;
    }

    @Override
    public void onReceive(PacketArguments packetArguments) {

    }

    @Override
    public PacketArguments getArguments() {
        return new PacketArguments().setArgument("player", String.class);
    }
}
