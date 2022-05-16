package me.logicologist.wordiple.server.packets.game;

import com.olziedev.olziesocket.framework.PacketArguments;
import com.olziedev.olziesocket.framework.api.packet.PacketAdapter;
import me.logicologist.wordiple.common.packets.AuthPacketType;
import me.logicologist.wordiple.common.queue.QueueType;

public class GameEndPacket extends PacketAdapter implements AuthPacketType {

    public GameEndPacket() {
        super("game_end_packet");
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
                .setArgument("type", QueueType.class)
                .setArgument("winner", String.class)
                .setArgument("scoredisplay", String.class)
                .setArgument("rating", Integer.class)
                .setArgument("ratingchange", Integer.class)
                .setArgument("rank", String.class)
                .setArgument("ratingtorankup", Integer.class)
                .setArgument("newexperience", Integer.class)
                .setArgument("newlevel", Integer.class)
                .setArgument("requiredexperience", Integer.class);
    }
}
