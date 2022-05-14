package me.logicologist.wordiple.client.packets.game;

import com.olziedev.olziesocket.framework.PacketArguments;
import com.olziedev.olziesocket.framework.api.packet.PacketAdapter;
import me.logicologist.wordiple.common.packets.AuthPacketType;

public class UpdateOppDisplayPacket extends PacketAdapter implements AuthPacketType {

    public UpdateOppDisplayPacket() {
        super("update_opp_display_packet");
        this.packetType = this;
    }

    @Override
    public void onReceive(PacketArguments packetArguments) {

    }

    @Override
    public PacketArguments getArguments() {
        return new PacketArguments()
                .setArgument("name", String.class)
                .setArgument("length", Integer.class)
                .setArgument("guess", Integer.class);
    }
}
