package me.logicologist.wordiple.client.sound;

import me.logicologist.wordiple.client.manager.SoundManager;

public enum SoundType {

    BUTTON_CLICK("button_click.wav", 1f),
    BACKGROUND_MUSIC("background_music.wav", 0.22f, true);

    private final String fileName;
    private final float volume;
    private final boolean repeat;

    SoundType(String fileName, float volume) {
        this(fileName, volume, false);
    }

    SoundType(String fileName, float volume, boolean repeat) {
        this.fileName = fileName;
        this.volume = volume;
        this.repeat = repeat;
    }

    public String getFileName() {
        return fileName;
    }

    public float getVolume() {
        return volume;
    }

    public boolean isRepeat() {
        return repeat;
    }

    public Sound getSound() {
        return SoundManager.getInstance().getSound(this);
    }
}
