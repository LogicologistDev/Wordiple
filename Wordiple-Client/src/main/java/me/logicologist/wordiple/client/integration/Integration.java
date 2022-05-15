package me.logicologist.wordiple.client.integration;

import me.logicologist.wordiple.client.manager.IntegrationManager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class Integration {

    protected static IntegrationManager manager = IntegrationManager.getInstance();

    public abstract void load() throws Exception;

    public abstract void update(IntegrationStatus status);

    public abstract void unload();
}
