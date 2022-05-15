package me.logicologist.wordiple.client.integration;

public abstract class Integration {

    public abstract void load() throws Exception;

    public abstract void update(IntegrationStatus status);

    public abstract void unload();
}
