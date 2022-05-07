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
        SessionManager manager = SessionManager.getInstance();
        boolean validCode = manager.isResetCodeValid(packetArguments.get("email", String.class), packetArguments.get("code", String.class));
        if (!validCode) {
            this.sendPacket(packet -> packetArguments.replace(this.getArguments()).setValues("response", "Invalid code. Please try again."));
            return;
        }
        boolean validSalt = DatabaseManager.instance.setPassword(packetArguments.get("email", String.class), packetArguments.get("salt", String.class), packetArguments.get("password_hash", String.class));
        if (!validSalt) {
            this.sendPacket(packet -> packetArguments.replace(this.getArguments()).setValues("response", "Invalid salt. Please try again."));
            return;
        }
        manager.logoutMatchingUsers(DatabaseManager.instance.getUUID(packetArguments.get("email", String.class)));
        this.sendPacket(packet -> packetArguments.replace(this.getArguments()).setValues("response", "Success"));
    }

    @Override
    public PacketArguments getArguments() {
        return new PacketArguments()
                .setArgument("email", String.class)
                .setArgument("response", String.class)
                .setArgument("code", String.class)
                .setArgument("salt", String.class)
                .setArgument("password_hash", String.class);
    }
}
