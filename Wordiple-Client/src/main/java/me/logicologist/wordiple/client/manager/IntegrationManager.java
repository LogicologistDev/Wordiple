package me.logicologist.wordiple.client.manager;

import me.logicologist.wordiple.client.integration.DiscordPresenceIntegration;
import me.logicologist.wordiple.client.integration.Integration;
import me.logicologist.wordiple.client.integration.IntegrationStatus;

import java.util.ArrayList;
import java.util.List;

public class IntegrationManager {

    private static IntegrationManager instance;

    private final List<Integration> integrations;

    public IntegrationManager() {
        integrations = new ArrayList<>();
        instance = this;
    }

    public void load() {
        this.integrations.add(new DiscordPresenceIntegration());
        this.integrations.forEach(x -> {
            try {
                x.load();
            } catch (Throwable ex) {
                ex.printStackTrace();
            }
        });
    }

    public static IntegrationManager getInstance() {
        return instance;
    }

    public void unload(boolean shutdown) {
        this.integrations.forEach(x -> {
            try {
                x.unload(shutdown);
            } catch (Throwable ex) {
                ex.printStackTrace();
            }
        });
    }

    public void update(IntegrationStatus status) {
        this.integrations.forEach(x -> {
            try {
                x.update(status);
            } catch (Throwable ex) {
                ex.printStackTrace();
            }
        });
    }
}
