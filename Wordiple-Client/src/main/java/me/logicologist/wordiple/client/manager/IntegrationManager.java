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
        this.integrations.forEach(Integration::load);
    }

    public IntegrationManager getInstance() {
        return instance;
    }

    public void unload() {
        this.integrations.forEach(Integration::unload);
    }

    public void update(IntegrationStatus status) {
        this.integrations.forEach(x -> x.update(status));
    }
}
