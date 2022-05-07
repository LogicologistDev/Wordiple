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
    public void onReceive(PacketArguments arguments) {
        this.sendPacket(packet -> arguments.replace(this.getArguments())
                .setValues("username", "")
                .setValues("games_played", "")
                .setValues("wins", "")
                .setValues("losses", "")
                .setValues("playtime", "")
                .setValues("curren_session", "")
                .setValues("season", "")
                .setValues("current_rank", "")
                .setValues("current_rating", "")
                .setValues("highest_rank", "")
                .setValues("highest_rating", "")
                .setValues("solve_time", "")
                .setValues("opener", "")
                .setValues("level", "")
                .setValues("total_xp", "")
                .setValues("guesses", ""));
    }

    @Override
    public PacketArguments getArguments() {
        return new PacketArguments()
                .setArgument("username", String.class)
                .setArgument("games_played", String.class)
                .setArgument("wins", String.class)
                .setArgument("losses", String.class)
                .setArgument("playtime", String.class)
                .setArgument("curren_session", String.class)
                .setArgument("season", String.class)
                .setArgument("current_rank", String.class)
                .setArgument("current_rating", String.class)
                .setArgument("highest_rank", String.class)
                .setArgument("highest_rating", String.class)
                .setArgument("solve_time", String.class)
                .setArgument("opener", String.class)
                .setArgument("level", String.class)
                .setArgument("total_xp", String.class)
                .setArgument("guesses", String.class);
    }
}
