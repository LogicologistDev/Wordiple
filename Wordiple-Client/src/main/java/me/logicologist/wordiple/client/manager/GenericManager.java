package me.logicologist.wordiple.client.manager;

public class GenericManager {

    public static void loadManagers(boolean developerMode) {
        // this is to reduce the code in the main class, due to the "requirement"
        new SessionManager();
        new PacketManager(developerMode).load();
        new WordManager();
        new IntegrationManager().load();
    }
}
