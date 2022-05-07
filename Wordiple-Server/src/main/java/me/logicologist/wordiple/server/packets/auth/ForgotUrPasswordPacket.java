package me.logicologist.wordiple.server.packets.auth;

import com.olziedev.olziesocket.framework.PacketArguments;
import com.olziedev.olziesocket.framework.api.packet.PacketAdapter;
import com.olziedev.olziesocket.framework.api.packet.PacketType;
import me.logicologist.wordiple.server.managers.DatabaseManager;
import me.logicologist.wordiple.server.managers.SessionManager;

import java.util.UUID;

public class ForgotUrPasswordPacket extends PacketAdapter implements PacketType {

    public ForgotUrPasswordPacket() {
        super("forgot_ur_password_packet");
        this.packetType = this;
    }

    @Override
    public boolean onlySendToServer() {
        return true;
    }

    @Override
    public void onReceive(PacketArguments arguments) {
        String email = arguments.get("email", String.class);
        if (DatabaseManager.instance.emailAvailable(email)) {
            this.sendPacket(packet -> arguments.replace(this.getArguments()).setValues("response", null));
            return;
        }
        UUID code = UUID.randomUUID();
        SessionManager.getInstance().resetPassword(email, code, DatabaseManager.instance.getUsername(email));
        this.sendPacket(packet -> arguments.replace(this.getArguments()).setValues("response", code.toString()));
    }

    @Override
    public PacketArguments getArguments() {
        return new PacketArguments()
                .setArgument("email", String.class)
                .setArgument("response", String.class);
    }
}
