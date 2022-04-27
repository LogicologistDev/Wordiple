package me.logicologist.wordiple.server;

import com.olziedev.olziesocket.framework.PacketArguments;
import me.logicologist.wordiple.server.managers.DatabaseManager;
import me.logicologist.wordiple.server.managers.PacketManager;
import me.logicologist.wordiple.server.managers.SessionManager;

import java.util.Scanner;
import java.util.UUID;

public class WordipleServer {

    public static void main(String[] args) {
        new PacketManager().load();
        new DatabaseManager().setup();
        new SessionManager();
    }
}
