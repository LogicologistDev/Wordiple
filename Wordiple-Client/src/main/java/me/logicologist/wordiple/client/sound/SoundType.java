package me.logicologist.wordiple.client.sound;

import me.logicologist.wordiple.client.WordipleClient;
import me.logicologist.wordiple.client.manager.SoundManager;

import java.io.File;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public enum SoundType {

    BUTTON_CLICK("https://www.dropbox.com/s/iepp33i8d0vi3tk/button_click.wav?dl=1", "button_click.wav", 1f),

    OVERWORLD("https://www.dropbox.com/s/8bbg2g80c4b6fu6/overworld.wav?dl=1", "overworld.wav", 0.22f),
    BACKGROUND("https://www.dropbox.com/s/s7a261q3dctic50/background_music.wav?dl=1", "background_music.wav", 0.22f, true),
    BACKGROUND_MUSIC(OVERWORLD, BACKGROUND),

    DANCE_OF_THE_DERPY_CHICKEN("https://www.dropbox.com/s/1ovi11uh6i7nh1r/dance_of_the_derpy_chicken.wav?dl=1", "dance_of_the_derpy_chicken.wav", 0.22f, false),
    END_OF_FRIENDSHIP("https://www.dropbox.com/s/l8vg1vqivfyja81/end_of_friendship.wav?dl=1", "end_of_friendship.wav", 0.22f, false),
    GAELIC_CONCLUSION("https://www.dropbox.com/s/unxklg86zbh383q/gaelic_conclusion.wav?dl=1", "gaelic_conclusion.wav", 0.22f, false),
    JOE_LOOKS_CONCERNED("https://www.dropbox.com/s/u0rcvwiz99svsdf/joe_looks_concerned.wav?dl=1", "joe_looks_concerned.wav", 0.22f, false),
    MOVE_OR_DIE("https://www.dropbox.com/s/d2yq6lcu3tblsc9/move_or_die.wav?dl=1", "move_or_die.wav", 0.22f, false),
    TICK_TOCK("https://www.dropbox.com/s/q1912ts2zukkgpb/tick_tock.wav?dl=1", "tick_tock.wav", 0.22f, false),
    WHATS_UP_FURBALL("https://www.dropbox.com/s/8gg5ki4ipip6x9i/whats_up_furball.wav?dl=1", "whats_up_furball.wav", 0.22f, false),
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

    public void download(String version, String localVersion) {
        if (this.url == null) return;


        try {
            File file = new File(WordipleClient.getAppData() + File.separator + "sounds", fileName);
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }

            boolean exists = file.exists();
            if (exists && version.equals(localVersion)) {
                return;
            }
            if (!exists) {
                file.createNewFile();
            }
            HttpURLConnection httpcon = (HttpURLConnection) new URL(url).openConnection();
            httpcon.addRequestProperty("User-Agent", "Mozilla/4.0");
            httpcon.setConnectTimeout(5 * 1000);
            httpcon.setReadTimeout(5 * 1000);

//            long fileModified = file.lastModified();
//            long urlModified = httpcon.getLastModified();
//
//            if (urlModified == fileModified) {
//                WordipleClient.getLogger().info("Sound file " + fileName + " is up to date. local: " + fileModified + " url: " + urlModified);
//                return;
//            }

            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }

            if (!file.exists()) {
                file.createNewFile();
            }

            //DONWLOAD HERE
            WordipleClient.getLogger().info("Downloaded sound file " + fileName);
            Files.copy(httpcon.getInputStream(), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception ex) {
            ex.printStackTrace();
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
