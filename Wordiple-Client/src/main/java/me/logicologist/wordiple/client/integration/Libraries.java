package me.logicologist.wordiple.client.integration;

import me.logicologist.wordiple.client.WordipleClient;

import java.io.File;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public enum Libraries {

    DISCORD_PRESENCE("https://www.dropbox.com/s/21y28ul3m2d0xzf/discord-rpc.jar?dl=1", "discord-rpc.jar"),
    UPDATER("https://www.dropbox.com/s/1x8j9j9j9j9j9j9/Updater.jar?dl=1", "Updater.jar");

    private final String url;
    private final File file;

    Libraries(String url, String fileName) {
        this.url = url;
        this.file = fileName == null ? null : new File(WordipleClient.getAppData() + File.separator + "libraries", fileName);
        if (file != null && !file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
    }

    public String getUrl() {
        return url;
    }

    public File getFile() {
        return this.file;
    }

    public boolean needDownload(String version, String localVersion) {
        if (file == null) return false;

        boolean exists = file.exists();
        return !exists || !version.equals(localVersion);
    }

    public void download(Runnable assetsInsert) {
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
            WordipleClient.getLogger().info("Downloaded library " + this.file);
            Files.copy(httpcon.getInputStream(), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
            assetsInsert.run();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
