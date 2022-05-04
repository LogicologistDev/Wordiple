package me.logicologist.wordiple.server.packets;

import com.olziedev.olziesocket.framework.PacketArguments;
import com.olziedev.olziesocket.framework.api.packet.PacketAdapter;
import com.olziedev.olziesocket.framework.api.packet.PacketType;
import me.logicologist.wordiple.server.managers.SessionManager;

public class SignupPacket extends PacketAdapter implements PacketType {
    public SignupPacket() {
        super("signup_packet");
        this.packetType = this;
    }

    @Override
    public boolean onlySendToServer() {
        return true;
    }

    @Override
    public void onReceive(PacketArguments packetArguments) {
        this.sendPacket(packet -> packetArguments.replace(this.getArguments()).setValues("response", SessionManager.getInstance().createSignupSession(packetArguments)));
    }

    @Override
    public PacketArguments getArguments() {
        return new PacketArguments()
                .setArgument("email", String.class)
                .setArgument("username", String.class)
                .setArgument("password", String.class)
                .setArgument("response", String.class);
    }
}
