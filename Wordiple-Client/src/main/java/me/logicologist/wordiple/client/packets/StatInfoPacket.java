package me.logicologist.wordiple.client.packets;

import com.olziedev.olziesocket.framework.PacketArguments;
import com.olziedev.olziesocket.framework.api.packet.PacketAdapter;
import com.olziedev.olziesocket.framework.api.packet.PacketType;

public class StatInfoPacket extends PacketAdapter implements PacketType {

    public StatInfoPacket() {
        super("stat_info_packet");
        this.packetType = this;
    }

    @Override
    public void onReceive(PacketArguments packetArguments) {

    }

    @Override
    public PacketArguments getArguments() {
        return null;
    }
}
