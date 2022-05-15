package me.logicologist.wordiple.client.integration;

import me.logicologist.wordiple.client.WordipleClient;
import net.arikia.dev.drpc.DiscordEventHandlers;
import net.arikia.dev.drpc.DiscordRPC;
import net.arikia.dev.drpc.DiscordRichPresence;

public class DiscordPresenceIntegration extends Integration {

    @Override
    public void load() throws Exception {
        DiscordEventHandlers handlers = new DiscordEventHandlers.Builder().setReadyEventHandler((user) -> {
            WordipleClient.getLogger().info("AHHAHAHAHAHAHA");
        }).setErroredEventHandler((e, e2) -> WordipleClient.getLogger().info(e2)).build();
        DiscordRPC.discordInitialize("975144169225998346", handlers, true);
        DiscordRPC.discordRunCallbacks();
        DiscordRichPresence rich = new DiscordRichPresence.Builder("This is the current state.").setDetails("These are some details.").build();
        DiscordRPC.discordUpdatePresence(rich);
    }

    @Override
    public void update(IntegrationStatus status) {

    }

    @Override
    public void unload() {

    }
}
