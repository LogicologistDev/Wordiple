package me.logicologist.wordiple.client.sound;

import me.logicologist.wordiple.client.WordipleClient;
import me.logicologist.wordiple.client.manager.SoundManager;

import java.io.File;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public enum SoundType {

    BUTTON_CLICK("https://www.dropbox.com/s/iepp33i8d0vi3tk/button_click.wav?dl=1", "button_click.wav", 0.0f),

    OVERWORLD("https://www.dropbox.com/s/8bbg2g80c4b6fu6/overworld.wav?dl=1", "overworld.wav", -27),
    BACKGROUND("https://www.dropbox.com/s/s7a261q3dctic50/background_music.wav?dl=1", "background_music.wav", -27, true),
    VANILLA_FLAVORED_SODA("https://www.dropbox.com/s/hjlxarid3lys7ml/vanilla_flavored_soda.wav?dl=1", "vanilla_flavored_soda.wav", -27, true),
    GREAT_ESCAPE("https://www.dropbox.com/s/4nk296nkkaopdlp/great_escape.wav?dl=1", "great_escape.wav", -27, true),

    BACKGROUND_MUSIC(true, OVERWORLD, BACKGROUND, VANILLA_FLAVORED_SODA, GREAT_ESCAPE),

    DROPBUBBLE("https://www.dropbox.com/s/vw1k9lgozbtso7r/dropbubble.wav?dl=1", "dropbubble.wav", -27),
    TIMELINE("https://www.dropbox.com/s/o1kbisqvp7mymqv/timeline.wav?dl=1", "timeline.wav", -27, true),
    UP_IN_MY_JAM("https://www.dropbox.com/s/oiwl44jp3wz0ytw/up_in_my_jam.wav?dl=1", "up_in_my_jam.wav", -27, true),

    CALM_MUSIC(true, DROPBUBBLE, TIMELINE, UP_IN_MY_JAM),

    DANCE_OF_THE_DERPY_CHICKEN("https://www.dropbox.com/s/1ovi11uh6i7nh1r/dance_of_the_derpy_chicken.wav?dl=1", "dance_of_the_derpy_chicken.wav", -27, false),
    END_OF_FRIENDSHIP("https://www.dropbox.com/s/l8vg1vqivfyja81/end_of_friendship.wav?dl=1", "end_of_friendship.wav", -27, false),
    GAELIC_CONCLUSION("https://www.dropbox.com/s/unxklg86zbh383q/gaelic_conclusion.wav?dl=1", "gaelic_conclusion.wav", -27, false),
    JOE_LOOKS_CONCERNED("https://www.dropbox.com/s/u0rcvwiz99svsdf/joe_looks_concerned.wav?dl=1", "joe_looks_concerned.wav", -27, false),
    MOVE_OR_DIE("https://www.dropbox.com/s/d2yq6lcu3tblsc9/move_or_die.wav?dl=1", "move_or_die.wav", -27, false),
    TICK_TOCK("https://www.dropbox.com/s/q1912ts2zukkgpb/tick_tock.wav?dl=1", "tick_tock.wav", -27, false),
    WHATS_UP_FURBALL("https://www.dropbox.com/s/8gg5ki4ipip6x9i/whats_up_furball.wav?dl=1", "whats_up_furball.wav", -27, false),
    FIGHTING_MUSIC(true,
            DANCE_OF_THE_DERPY_CHICKEN,
            END_OF_FRIENDSHIP,
            GAELIC_CONCLUSION,
            JOE_LOOKS_CONCERNED,
            MOVE_OR_DIE,
            TICK_TOCK,
            WHATS_UP_FURBALL
    );


    private final String url;
    private final File file;
    private final float volume;
    private final boolean repeat;
    private final boolean fade;
    private SoundType[] children;

    SoundType(String url, String fileName, float volume) {
        this(url, fileName, volume, false, false);
    }

    SoundType(String url, String fileName, float volume, boolean fade) {
        this(url, fileName, volume, false, fade);
    }

    SoundType(String url, String fileName, float volume, boolean repeat, boolean fade) {
        this.url = url;
        this.file = fileName == null ? null : new File(WordipleClient.getAppData() + File.separator + "sounds", fileName);
        this.volume = volume;
        this.repeat = repeat;
        this.fade = fade;
        if (file != null && !file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
    }

    SoundType(boolean fade, SoundType... children) {
        this(null, null, -1, fade);
        this.children = children;
    }

    public boolean needDownload(String version, String localVersion) {
        if (file == null) return false;

        boolean exists = file.exists();
        return !exists || !version.equals(localVersion);
    }

    public void download(Runnable update) {
        if (this.url == null) {
            return;
        }
        try {
            if (!file.exists()) {
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

            //DONWLOAD HERE
            WordipleClient.getLogger().info("Downloaded sound file " + this.file);
            Files.copy(httpcon.getInputStream(), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
            update.run();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public File getFile() {
        return file;
    }

    public float getVolume() {
        return volume;
    }

    public boolean isRepeat() {
        return repeat;
    }

    public boolean isFade() {
        return fade;
    }

    public Sound getSound() {
        return SoundManager.getInstance().getSound(this);
    }

    public SoundType[] getChildren() {
        return this.children;
    }
}
