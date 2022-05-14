package me.logicologist.wordiple.client.manager;

import me.logicologist.wordiple.client.WordipleClient;
import me.logicologist.wordiple.client.integration.Integration;

import java.io.File;

public class GenericManager {

    public static void loadManagers(boolean developerMode) {
//        Integration.loadLibrary(new File(WordipleClient.getAppData(), "discord-rpc.jar"));
        // this is to reduce the code in the main class, due to the "requirement"
        new SessionManager();
        new PacketManager(developerMode).load();
        new WordManager();
        new IntegrationManager().load();
    }
}
