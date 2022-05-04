package me.logicologist.wordiple.client;

import me.logicologist.wordiple.client.manager.GUIManager;
import me.logicologist.wordiple.client.manager.PacketManager;
import me.logicologist.wordiple.client.manager.SessionManager;

public class WordipleClient {

    public static void main(String[] args) {
        new SessionManager();
        new PacketManager().load();

        GUIManager.launch(args);
    }
}
