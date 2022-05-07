package me.logicologist.wordiple.server.packets.auth;

import com.olziedev.olziesocket.framework.PacketArguments;
import com.olziedev.olziesocket.framework.api.packet.PacketAdapter;
import com.olziedev.olziesocket.framework.api.packet.PacketType;
import me.logicologist.wordiple.server.managers.DatabaseManager;

public class SaltRetrievePacket extends PacketAdapter implements PacketType {

    public SaltRetrievePacket() {
        super("salt_retrieve_packet");
        this.packetType = this;
    }

    @Override
    public boolean onlySendToServer() {
        return true;
    }

    @Override
    public void onReceive(PacketArguments packetArguments) {
        String username = packetArguments.get("username", String.class);
        String salt = DatabaseManager.instance.getSalt(username);
        this.sendPacket(packet -> packetArguments.replace(this.getArguments()).setValues("salt", salt));
    }

    @Override
    public PacketArguments getArguments() {
        return new PacketArguments()
                .setArgument("username", String.class)
                .setArgument("salt", String.class);
    }
}
