package me.logicologist.wordiple.client.sound;

import me.logicologist.wordiple.client.manager.SoundManager;

public enum SoundType {

    BUTTON_CLICK("button_click.wav", 1f),
    BACKGROUND_MUSIC("background_music.wav", 0.22f);

    private final String fileName;
    private final float volume;

    SoundType(String fileName, float volume) {
        this.fileName = fileName;
        this.volume = volume;
    }

    public String getFileName() {
        return fileName;
    }

    public float getVolume() {
        return volume;
    }

    public Sound getSound() {
        return SoundManager.getInstance().getSound(this);
    }
}
