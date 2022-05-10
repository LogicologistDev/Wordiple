package me.logicologist.wordiple.client.manager;

import javafx.application.Platform;
import me.logicologist.wordiple.client.WordipleClient;
import me.logicologist.wordiple.client.gui.controllers.LoadScreenController;
import me.logicologist.wordiple.client.sound.Sound;
import me.logicologist.wordiple.client.sound.SoundType;
import me.logicologist.wordiple.common.utils.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class SoundManager {

    private static SoundManager instance;

    private final List<Sound> sounds;

    public SoundManager() {
        instance = this;
        this.sounds = new ArrayList<>();
    }

    public void load(GUIManager guiManager, Runnable runnable) {
        SessionManager manager = SessionManager.getInstance();
        String version = Utils.getVersion();
        String localVersion = manager.getLocalSoundVersion();

        AtomicReference<LoadScreenController> controller = new AtomicReference<>();
        AtomicInteger downloaded = new AtomicInteger();
        long neededDownloaded = Arrays.stream(SoundType.values()).filter(x -> x.needDownload(version, localVersion)).count();
        if (neededDownloaded > 0) {
            controller.set(guiManager.showLoadScreen("Fetching Assets (0%)"));
        }
        WordipleClient.getExecutor().submit(() -> {
            WordipleClient.getLogger().info("Needed sounds: " + neededDownloaded);
            for (SoundType value : SoundType.values()) {
                value.download(version, localVersion, () -> {
                    float result = downloaded.get() == 0 ? 0 : ((float) downloaded.get() / (float) neededDownloaded) * 100.0f;
                    Platform.runLater(() -> {
                        String s = "Fetching Assets (" + ((int) result) + "%)";
                        controller.get().setText(s);
                    });
                });
                downloaded.incrementAndGet();
                this.sounds.add(new Sound(value));
            }
            if (controller.get() != null) controller.get().remove(null);

            manager.setLocalSoundVersion(version);
            Platform.runLater(runnable);
        });
    }

    public Sound getSound(SoundType type) {
        return this.sounds.stream().filter(x -> x.getType() == type).findFirst().orElse(null);
    }

    public void playSound(SoundType type) {
        Sound sound = this.getSound(type);
        if (sound == null) return;

        sound.play();
    }

    public void stopSound(SoundType type) {
        Sound sound = this.getSound(type);
        if (sound == null) return;

        sound.stop();
    }

    public void stopSounds() {
        this.sounds.forEach(Sound::stop);
    }

    public static SoundManager getInstance() {
        return instance;
    }
}
