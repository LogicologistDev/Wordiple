package me.logicologist.wordiple.client.packets.game;

import com.olziedev.olziesocket.framework.PacketArguments;
import com.olziedev.olziesocket.framework.api.packet.PacketAdapter;
import me.logicologist.wordiple.client.manager.GUIManager;
import me.logicologist.wordiple.client.manager.SessionManager;
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
        int time = (int) (((packetArguments.get("timerend", Long.class) - System.currentTimeMillis()) / 1000));
        GUIManager.getInstance().getGameController().startTimer(time, packetArguments.get("guesslimit", Integer.class));
        if (SessionManager.getInstance().getUsername().equals(packetArguments.get("player", String.class))) GUIManager.getInstance().getGameController().setAnswerLocked(true);
    }

    @Override
    public PacketArguments getArguments() {
        return new PacketArguments()
                .setArgument("player", String.class)
                .setArgument("timerend", Long.class)
                .setArgument("guesslimit", Integer.class);
    }
}
