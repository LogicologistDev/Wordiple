package me.logicologist.wordiple.client;

import com.sun.javafx.runtime.VersionInfo;
import me.logicologist.wordiple.client.manager.GUIManager;
import me.logicologist.wordiple.client.manager.PacketManager;

public class WordipleClient {

    public static void main(String[] args) {
        System.out.println(VersionInfo.getRuntimeVersion());
        new PacketManager().load();
        GUIManager.launch(args);
    }
}
