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
    private final String version;
    private final String localVersion;

    public SoundManager() {
        instance = this;
        this.sounds = new ArrayList<>();
        this.version = Utils.getVersion();
        this.localVersion = SessionManager.getInstance().getLocalSoundVersion();
    }

    public long neededDownloaded() {
        return Arrays.stream(SoundType.values()).filter(x -> x.needDownload(version, localVersion)).count();
    }

    public void load(GUIManager guiManager, Runnable runnable, long neededDownloaded) {
        if (sounds.size() == SoundType.values().length) {
            runnable.run();
            return;
        }
        if (neededDownloaded <= 0) {
            for (SoundType value : SoundType.values()) {
                this.sounds.add(new Sound(value));
            }
            return;
        }
        LoadScreenController controller = guiManager.showLoadScreen("Fetching Assets (0%)");
        WordipleClient.getExecutor().submit(() -> {
            AtomicInteger downloaded = new AtomicInteger();
            WordipleClient.getLogger().info("Needed sounds: " + neededDownloaded);
            for (SoundType value : SoundType.values()) {
                if (!value.needDownload(version, localVersion)) {
                    this.sounds.add(new Sound(value));
                    continue;
                }

                value.download(() -> {
                    float result = downloaded.get() == 0 ? 0 : ((float) downloaded.get() / (float) neededDownloaded) * 100.0f;
                    Platform.runLater(() -> {
                        String s = "Fetching Assets (" + ((int) result) + "%)";
                        controller.setText(s);
                    });
                });
                downloaded.incrementAndGet();
                this.sounds.add(new Sound(value));
            }
            controller.remove(null);

            SessionManager.getInstance().setLocalSoundVersion(version);
            WordipleClient.getLogger().info("Loaded sounds successfully");
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
