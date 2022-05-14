package me.logicologist.wordiple.client.integration;

import me.logicologist.wordiple.client.WordipleClient;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

public abstract class Integration {

    public abstract void load();

    public abstract void update(IntegrationStatus status);

    public abstract void unload();

}
