package me.logicologist.wordiple.client.manager;

import me.logicologist.wordiple.client.sound.Sound;
import me.logicologist.wordiple.client.sound.SoundType;

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
        for (SoundType value : SoundType.values()) {
            this.sounds.add(new Sound(value));
        }
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
