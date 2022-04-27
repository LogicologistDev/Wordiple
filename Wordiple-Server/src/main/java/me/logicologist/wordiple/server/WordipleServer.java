package me.logicologist.wordiple.server;

import com.olziedev.olziesocket.framework.PacketArguments;
import me.logicologist.wordiple.server.managers.DatabaseManager;
import me.logicologist.wordiple.server.managers.PacketManager;
import me.logicologist.wordiple.server.managers.SessionManager;

public class WordipleServer {

    public static void main(String[] args) {
        new PacketManager().load();
        new DatabaseManager().setup();
        new SessionManager();

        PacketArguments packetArguments = new PacketArguments()
                .setArgument("email", String.class)
                .setArgument("username", String.class)
                .setArgument("password", String.class)
                .setArgument("response", String.class);

        packetArguments
                .setValues("email", "thereallogicalyt@gmail.com")
                .setValues("username", "Logicologist")
                .setValues("password", "test");

        SessionManager.getInstance().createSignupSession(packetArguments);
    }
}
