package me.logicologist.wordiple.server.packets.game;

import com.olziedev.olziesocket.framework.PacketArguments;
import com.olziedev.olziesocket.framework.api.packet.PacketAdapter;
import me.logicologist.wordiple.common.packets.AuthPacketType;

public class StartTimerPacket extends PacketAdapter implements AuthPacketType {

    public StartTimerPacket() {
        super("start_timer_packet");
        this.packetType = this;
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
                .setArgument("player", String.class)
                .setArgument("timerend", Long.class)
                .setArgument("guesslimit", Integer.class);
    }
}
