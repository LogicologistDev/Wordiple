package me.logicologist.wordiple.client.sound;

import me.logicologist.wordiple.client.WordipleClient;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import java.net.URL;

public class Sound {

    private final SoundType type;
    private Clip clip;

    public Sound(SoundType type) {
        this.type = type;
        try {
            URL sound = getClass().getResource("/sounds/" + type.getFileName());
            WordipleClient.getLogger().info("Loading sound: " + sound);
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(sound);
            clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            this.setVolume(type.getVolume());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void setVolume(float volume) {
        if (volume < 0f || volume > 1f) return;

        FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
        gainControl.setValue(20f * (float) Math.log10(volume));
    }

    public void play() {
        // Stop clip if it's already running
        if (clip.isRunning()) return;

        // Rewind clip to beginning
        clip.setFramePosition(0);

        // Play clip
        clip.start();
    }

    public void stop() {
        clip.stop();
    }

    public SoundType getType() {
        return this.type;
    }
}
