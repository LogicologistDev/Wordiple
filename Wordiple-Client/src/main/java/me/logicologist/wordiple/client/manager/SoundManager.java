package me.logicologist.wordiple.client.manager;

import me.logicologist.wordiple.client.WordipleClient;
import me.logicologist.wordiple.client.sound.Sound;
import me.logicologist.wordiple.client.sound.SoundType;
import me.logicologist.wordiple.common.utils.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SoundManager {

    private static SoundManager instance;

    private final List<Sound> sounds;
    private final String version;
    private final String localVersion;

    public SoundManager() {
        instance = this;
        this.sounds = new ArrayList<>();
        this.version = Utils.getAssetVersion();
        this.localVersion = SessionManager.getInstance().getLocalAssetVersion();
    }

    public long neededDownloaded() {
        return Arrays.stream(SoundType.values()).filter(x -> x.needDownload(version, localVersion)).count();
    }

    public void load() {
        for (SoundType value : SoundType.values()) {
            if (!value.needDownload(version, localVersion)) {
                this.sounds.add(new Sound(value));
                continue;
            }
            value.download(GenericManager.assetsInsert);
            this.sounds.add(new Sound(value));
        }
        WordipleClient.getLogger().info("Loaded sounds successfully");
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
