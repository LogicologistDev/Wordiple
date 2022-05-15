package me.logicologist.wordiple.client.manager;

public class GenericManager {

    private static final Runnable assetsFinished = () -> {
        new IntegrationManager().load();

    };

    public static void loadManagers(boolean developerMode) {
        new SessionManager();
        new PacketManager(developerMode).load();
        new WordManager();
    }

    public static void downloadAssets() {
        long soundAmount = SoundManager.getInstance().neededDownloaded();
        long integrationAmount = IntegrationManager.getInstance().neededDownloaded();

        long finalAmount = soundAmount + integrationAmount;
        if (finalAmount <= 0) {
            assetsFinished.run();
            return;
        }

    }
}
