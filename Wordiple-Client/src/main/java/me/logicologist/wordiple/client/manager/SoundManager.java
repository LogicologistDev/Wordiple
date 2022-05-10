package me.logicologist.wordiple.client.manager;

import me.logicologist.wordiple.client.sound.Sound;
import me.logicologist.wordiple.client.sound.SoundType;
import me.logicologist.wordiple.common.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class SoundManager {

    private static SoundManager instance;

    private final List<Sound> sounds;

    public SoundManager() {
        instance = this;
        this.sounds = new ArrayList<>();
    }

    public void load() {
        SessionManager manager = SessionManager.getInstance();
        String version = Utils.getVersion();
        String localVersion = manager.getLocalSoundVersion();
        for (SoundType value : SoundType.values()) {
            value.download(version, localVersion);

            this.sounds.add(new Sound(value));
        }
        manager.setLocalSoundVersion(version);
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
