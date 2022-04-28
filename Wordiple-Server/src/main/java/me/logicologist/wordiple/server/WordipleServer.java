package me.logicologist.wordiple.server;

import com.olziedev.olziesocket.framework.PacketArguments;
import me.logicologist.wordiple.server.managers.DatabaseManager;
import me.logicologist.wordiple.server.managers.PacketManager;
import me.logicologist.wordiple.server.managers.SessionManager;

import java.util.Scanner;
import java.util.UUID;

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
                .setValues("email", "ian.michael.fan@gmail.com")
                .setValues("username", "KingFan")
                .setValues("password", "test");

        SessionManager.getInstance().createSignupSession(packetArguments);

        PacketArguments confirmPacket = new PacketArguments()
                .setArgument("code", String.class)
                .setArgument("email", String.class)
                .setArgument("response", UUID.class);

        System.out.println("Enter testing verification code: ");
        System.out.println("SID: " + SessionManager.getInstance().createNewAccount(new Scanner(System.in).nextLine(), "ian.michael.fan@gmail.com"));
    }
}
