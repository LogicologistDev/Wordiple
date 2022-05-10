package me.logicologist.wordiple.client.sound;

import me.logicologist.wordiple.client.WordipleClient;
import me.logicologist.wordiple.client.manager.SoundManager;

import java.io.File;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public enum SoundType {

    BUTTON_CLICK("", "button_click.wav", 1f),
    BACKGROUND_MUSIC("", "background_music.wav", 0.22f, true),
    DANCE_OF_THE_DERPY_CHICKEN("", "dance_of_the_derpy_chicken.wav", 0.22f, false),
    END_OF_FRIENDSHIP("", "end_of_friendship.wav", 0.22f, false),
    GAELIC_CONCLUSION("", "gaelic_conclusion.wav", 0.22f, false),
    JOE_LOOKS_CONCERNED("", "joe_looks_concerned.wav", 0.22f, false),
    MOVE_OR_DIE("", "move_or_die.wav", 0.22f, false),
    TICK_TOCK("", "tick_tock.wav", 0.22f, false),
    WHATS_UP_FURBALL("", "whats_up_furball.wav", 0.22f, false),
    FIGHTING_MUSIC(
            DANCE_OF_THE_DERPY_CHICKEN,
            END_OF_FRIENDSHIP,
            GAELIC_CONCLUSION,
            JOE_LOOKS_CONCERNED,
            MOVE_OR_DIE,
            TICK_TOCK,
            WHATS_UP_FURBALL
    );


    private final String url;
    private final String fileName;
    private final float volume;
    private final boolean repeat;
    private SoundType[] children;

    SoundType(String url, String fileName, float volume) {
        this(url, fileName, volume, false);
    }

    SoundType(String url, String fileName, float volume, boolean repeat) {
        this.url = url;
        this.fileName = fileName;
        this.volume = volume;
        this.repeat = repeat;
    }

    SoundType(SoundType... children) {
        this(null, null, -1, false);
        this.children = children;
    }

    public void download() {
        if (this.url == null) return;

        try {
            File file = new File(WordipleClient.getAppData() + File.separator + "sounds" + fileName);
            HttpURLConnection httpcon = (HttpURLConnection) new URL(url).openConnection();
            httpcon.addRequestProperty("User-Agent", "Mozilla/4.0");
            httpcon.setConnectTimeout(5 * 1000);
            httpcon.setReadTimeout(5 * 1000);

            long fileModified = file.lastModified();
            long urlModified = httpcon.getLastModified();
            if (urlModified == fileModified) {
                return;
            }

            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }

            if (!file.exists()) {
                file.createNewFile();
            }

            //DONWLOAD HERE
            Files.copy(httpcon.getInputStream(), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    public SoundType[] getChildren() {
        return this.children;
    }
}
