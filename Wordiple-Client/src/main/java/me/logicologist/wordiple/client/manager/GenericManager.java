package me.logicologist.wordiple.client.manager;

import javafx.application.Platform;
import me.logicologist.wordiple.client.WordipleClient;
import me.logicologist.wordiple.client.gui.controllers.LoadScreenController;
import me.logicologist.wordiple.common.utils.Utils;

import java.util.concurrent.atomic.AtomicInteger;

public class GenericManager {

    public static Runnable assetsInsert;

    public static void loadManagers(boolean developerMode, Runnable runnable) {
        WordipleClient.getLogger().info("AppData: " + WordipleClient.getAppData().getAbsolutePath());
        new SessionManager();
        new PacketManager(developerMode).load();
        new WordManager();
        runnable.run();
    }

    public static void downloadAssets(GUIManager guiManager) {
        Runnable assetsFinished = () -> {
            new IntegrationManager().load();
            SessionManager.getInstance().setAssetVersion(Utils.getAssetVersion());
            Platform.runLater(() -> GUIManager.innit(guiManager));
        };
        long soundAmount = SoundManager.getInstance().neededDownloaded();
        long libraryAmount = LibraryManager.getInstance().neededDownloaded();

        WordipleClient.getLogger().info("Sound amount: " + soundAmount);
        WordipleClient.getLogger().info("Library amount: " + libraryAmount);

        long finalAmount = soundAmount + libraryAmount;
        WordipleClient.getLogger().info("Downloading " + finalAmount + " assets...");
        if (finalAmount <= 0) {
            assetsFinished.run();
            return;
        }
        LoadScreenController controller = guiManager.showLoadScreen("Fetching Assets (0%)");
        AtomicInteger counter = new AtomicInteger(0);
        assetsInsert = () -> {
            float result = ((float) counter.getAndIncrement() / (float) finalAmount) * 100.0f;
            Platform.runLater(() -> {
                String s = "Fetching Assets (" + ((int) result) + "%)";
                controller.setText(s);
                if (counter.get() != finalAmount) return;

                controller.remove(null);
                assetsFinished.run();
            });
        };
    }
}
