package me.logicologist.wordiple.server.packets.auth;

import com.olziedev.olziesocket.framework.PacketArguments;
import com.olziedev.olziesocket.framework.api.packet.PacketAdapter;
import com.olziedev.olziesocket.framework.api.packet.PacketType;
import me.logicologist.wordiple.server.managers.DatabaseManager;
import me.logicologist.wordiple.server.managers.SessionManager;

public class ResetUrPasswordPacket extends PacketAdapter implements PacketType {

    public ResetUrPasswordPacket() {
        super("reset_ur_password_packet");
        this.packetType = this;
    }

    @Override
    public boolean onlySendToServer() {
        return true;
    }

    @Override
    public void onReceive(PacketArguments packetArguments) {
        SessionManager.getInstance().logoutMatchingUsers(DatabaseManager.instance.getUUID(packetArguments.get("email", String.class)));
        DatabaseManager.instance.setPassword(packetArguments.get("email", String.class), packetArguments.get("password", String.class));
        this.sendPacket(packet -> packetArguments.replace(this.getArguments()));
    }

    @Override
    public PacketArguments getArguments() {
        return new PacketArguments()
                .setArgument("email", String.class)
                .setArgument("code", String.class)
                .setArgument("password", String.class);
    }
}
