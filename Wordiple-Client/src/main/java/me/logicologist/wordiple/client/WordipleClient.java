package me.logicologist.wordiple.client;

import me.logicologist.wordiple.client.manager.GUIManager;
import me.logicologist.wordiple.client.manager.PacketManager;
import me.logicologist.wordiple.client.manager.SessionManager;
import me.logicologist.wordiple.client.packets.UserInfoPacket;

import java.util.concurrent.TimeUnit;

public class WordipleClient {

    public static void main(String[] args) {
        new SessionManager();
        new PacketManager().load();

        GUIManager.launch(args);
    }
}
