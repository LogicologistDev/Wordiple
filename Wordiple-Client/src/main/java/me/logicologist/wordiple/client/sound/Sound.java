package me.logicologist.wordiple.client.sound;

import me.logicologist.wordiple.client.WordipleClient;
import me.logicologist.wordiple.client.manager.PacketManager;

import javax.sound.sampled.*;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Sound {

    private final SoundType type;
    private Clip clip;

    private List<SoundType> playedSounds;
    private List<SoundType> children;

    public Sound(SoundType type) {
        this.type = type;
        try {
            clip = AudioSystem.getClip();
            if (type.getChildren() != null) {
                this.playedSounds = new ArrayList<>();
                this.children = new ArrayList<>(Arrays.asList(type.getChildren()));
                Collections.shuffle(children);
                this.playedSounds.add(children.get(0));

                clip.addLineListener(event -> {
                    if (event.getType() != LineEvent.Type.STOP || this.children == null) return;
                    if (children.size() == playedSounds.size()) playedSounds.clear();

                    SoundType nextSound;
                    do {
                        nextSound = children.get(playedSounds.size());
                    } while (playedSounds.contains(nextSound));

                    this.loadSound(nextSound);
                    this.play();
                });
            }
            this.loadSound(this.playedSounds == null ? type : this.playedSounds.get(0));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void loadSound(SoundType type) {
        try {
            File sound = new File(WordipleClient.getAppData() + File.separator + "sounds", type.getFileName());
            WordipleClient.getLogger().info("Loading sound: " + sound);
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(sound);
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
        try {
            // Stop clip if it's already running
            if (clip.isRunning()) return;

            // Rewind clip to beginning
            clip.setFramePosition(0);

            // Play clip
            clip.start();

            if (!type.isRepeat()) return;

            clip.loop(Clip.LOOP_CONTINUOUSLY);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void stop() {
        try {
            this.children = null;
            clip.stop();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public SoundType getType() {
        return this.type;
    }
}
