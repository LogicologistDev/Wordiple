package me.logicologist.wordiple.server.packets;

import com.olziedev.olziesocket.framework.PacketArguments;
import com.olziedev.olziesocket.framework.api.packet.PacketAdapter;
import com.olziedev.olziesocket.framework.api.packet.PacketType;
import me.logicologist.wordiple.server.managers.SessionManager;

import java.util.UUID;

public class SignupConfirmPacket extends PacketAdapter implements PacketType {

    public SignupConfirmPacket() {
        super("signup_confirm_packet");
        this.packetType = this;
    }

    @Override
    public void onReceive(PacketArguments packetArguments) {
        String email = packetArguments.get("email", String.class);
        String code = packetArguments.get("code", String.class);
        UUID sessionId = SessionManager.getInstance().createNewAccount(code, email);
        this.sendPacket(packet -> packetArguments.replace(this.getArguments()).setValues("response", sessionId));
    }

    @Override
    public PacketArguments getArguments() {
        return new PacketArguments()
                .setArgument("code", String.class)
                .setArgument("email", String.class)
                .setArgument("response", UUID.class);
    }
}