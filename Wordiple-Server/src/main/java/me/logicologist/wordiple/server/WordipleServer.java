package me.logicologist.wordiple.server;

import me.logicologist.wordiple.server.managers.PacketManager;

public class WordipleServer {

    public static void main(String[] args) {
        new PacketManager().load();
    }
}
