package me.logicologist.wordiple.server.managers;

import me.logicologist.wordiple.server.packets.LoginPacket;
import me.logicologist.wordiple.server.user.WordipleUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class SessionManager {

    private static SessionManager instance;
    private final HashMap<UUID, WordipleUser> sessions;

    public SessionManager() {
        this.sessions = new HashMap<>();
        instance = this;
    }

    public WordipleUser getSessionFromToken(UUID token) {
        if (sessions.containsKey(token)) {
            return sessions.get(token);
        }
        return null;
    }

    public UUID createSession(String username, String password) {
        if (!DatabaseManager.instance.validateLogin(username, password)) {
            return null;
        }
        WordipleUser user = DatabaseManager.instance.constructWordipleUser(username);
        List<UUID> invalidSessionIds = new ArrayList<>();
        sessions.forEach((k, v) -> {
            if (v.getId().equals(user.getId())) invalidSessionIds.add(k);
        });
        for (UUID sessionId : invalidSessionIds) {
            this.sessions.remove(sessionId);
        }
        UUID newSessionId = UUID.randomUUID();
        while (this.sessions.containsKey(newSessionId)) {
            newSessionId = UUID.randomUUID();
        }
        this.sessions.put(newSessionId, user);
        return newSessionId;
    }

    public static SessionManager getInstance() {
        return instance;
    }

}
