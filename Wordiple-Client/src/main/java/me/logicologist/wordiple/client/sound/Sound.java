package me.logicologist.wordiple.client.sound;

import me.logicologist.wordiple.client.WordipleClient;

import javax.sound.sampled.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Sound {

    private final SoundType type;
    private SoundType selected;
    private Clip clip;

    private List<SoundType> playedSounds;
    private List<SoundType> children;

    public Sound(SoundType type) {
        this.type = type;
        try {
            if (type.getChildren() != null) {
                this.playedSounds = new ArrayList<>();
                this.children = new ArrayList<>(Arrays.asList(type.getChildren()));
                Collections.shuffle(children);
            }
            this.loadSound(this.children == null ? type : this.children.get(0));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void loadSound(SoundType type) {
        try {
            this.selected = type;
            clip = AudioSystem.getClip();
            if (this.children != null) {
                clip.addLineListener(event -> {
                    if (event.getType() != LineEvent.Type.STOP || this.children == null) return;
                    if (playedSounds.size() + 1 >= this.children.size()) {
                        WordipleClient.getLogger().info("Resetting children");
                        playedSounds.clear();
                    }

                    SoundType nextSound = children.get(playedSounds.size());
                    WordipleClient.getLogger().info("Finding sound: " + nextSound.getFile());

                    this.loadSound(nextSound);
                    this.play();
                });
            }
            if (this.playedSounds != null) this.playedSounds.add(type);
            WordipleClient.getLogger().info("Loading sound: " + type.getFile());
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(type.getFile());
            clip.open(audioInputStream);
            this.setVolume(this.type.isFade() ? -60 : type.getVolume());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void setVolume(float volume) {
        FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
        gainControl.setValue(volume);

    }

    public void play() {
        WordipleClient.getLogger().info("Playing sound: " + this.selected.getFile());
        try {
            WordipleClient.getLogger().info("Clip running: " + this.clip.isRunning());
            // Stop clip if it's already running
            if (clip.isRunning()) return;

            WordipleClient.getLogger().info("Clip playing: " + this.clip.isRunning());
            clip.setFramePosition(0);
            clip.start();

            if (this.type.isFade()) {
                WordipleClient.getExecutor().submit(() -> {
                    try {
                        FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                        float current = gainControl.getValue();
                        WordipleClient.getLogger().info(selected.getVolume());
                        float targetDB = selected.getVolume();
                        WordipleClient.getLogger().info(current);
                        while (current < targetDB) {
                            current += 0.1;
                            gainControl.setValue(current);
                            try {
                                Thread.sleep(10);
                            } catch (Exception ignored) {
                            }
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                });
            }
            if (!type.isRepeat()) return;

            clip.loop(Clip.LOOP_CONTINUOUSLY);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void stop() {
        try {
            this.children = null;

            if (!this.type.isFade() || WordipleClient.getExecutor().isShutdown()) {
                clip.stop();
                return;
            }
            WordipleClient.getExecutor().submit(() -> {
                FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                float current = gainControl.getValue();
                WordipleClient.getLogger().info(selected.getVolume());
                float targetDB = -60;
                while (current > targetDB) {
                    current -= 0.1;
                    gainControl.setValue(current);
                    try {
                        Thread.sleep(10);
                    } catch (Exception ignored) {}
                }
                clip.stop();
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public SoundType getType() {
        return this.type;
    }
}
