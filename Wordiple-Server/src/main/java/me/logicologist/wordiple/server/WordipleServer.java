package me.logicologist.wordiple.server;

import me.logicologist.wordiple.server.managers.DatabaseManager;
import me.logicologist.wordiple.server.managers.PacketManager;
import me.logicologist.wordiple.server.managers.SessionManager;

public class WordipleServer {

    public static void main(String[] args) {
        new PacketManager().load();
        new DatabaseManager().setup();
        new SessionManager();

    }
}
