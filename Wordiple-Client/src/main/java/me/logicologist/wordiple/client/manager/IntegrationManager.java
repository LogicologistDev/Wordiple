package me.logicologist.wordiple.client.manager;

import me.logicologist.wordiple.client.WordipleClient;
import me.logicologist.wordiple.client.integration.DiscordPresenceIntegration;
import me.logicologist.wordiple.client.integration.Integration;
import me.logicologist.wordiple.client.integration.IntegrationStatus;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarFile;

public class IntegrationManager {

    private static IntegrationManager instance;

    private final List<Integration> integrations;

    public IntegrationManager() {
        integrations = new ArrayList<>();
        instance = this;
    }

    public void load() {
        File file = new File(WordipleClient.getAppData() + File.separator + "integrations");
        try {
            for (File listFile : file.listFiles(x -> x.getName().endsWith(".jar"))) {
                try {
                    addToClassPath(listFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (Throwable e) {
            WordipleClient.getLogger().info("Running jar in IDE mode.");
        }
        this.integrations.add(new DiscordPresenceIntegration());
        this.integrations.forEach(x -> {
            try {
                x.load();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }

    public long neededDownloaded() {
        return 0;
    }

    /**
     * Adds a JAR file to the list of JAR files searched by the system class
     * loader. This effectively adds a new JAR to the class path.
     *
     * @param jarFile the JAR file to add
     * @throws IOException if there is an error accessing the JAR file
     */
    public static synchronized void addToClassPath(File jarFile) throws IOException {
        if (jarFile == null) {
            throw new NullPointerException();
        }
        // do our best to ensure consistent behaviour across methods
        if (!jarFile.exists()) {
            throw new FileNotFoundException(jarFile.getAbsolutePath());
        }
        if (!jarFile.canRead()) {
            throw new IOException("can't read jar: " + jarFile.getAbsolutePath());
        }
        if (jarFile.isDirectory()) {
            throw new IOException("not a jar: " + jarFile.getAbsolutePath());
        }

        // add the jar using instrumentation, or fall back to reflection
        if (inst != null) {
            inst.appendToSystemClassLoaderSearch(new JarFile(jarFile));
            return;
        }
        try {
            getAddUrlMethod().invoke(addUrlThis, jarFile.toURI().toURL());
        } catch (SecurityException iae) {
            throw new RuntimeException("security model prevents access to method", iae);
        } catch (Throwable t) {
            // IllegalAccessException
            // IllegalArgumentException
            // InvocationTargetException
            // MalformedURLException
            // (or a runtime error)
            throw new AssertionError("internal error", t);
        }
    }

    /**
     * Returns whether the extending the class path is supported on the host
     * JRE. If this returns false, the most likely causes are:
     * <ul>
     * <li> the manifest is not configured to load the agent or the
     * {@code -javaagent:jarpath} argument was not specified (Java 9+);
     * <li> security restrictions are preventing reflective access to the class
     * loader (Java &le; 8);
     * <li> the underlying VM neither supports agents nor uses URLClassLoader as
     * its system class loader (extremely unlikely from Java 1.6+).
     * </ul>
     *
     * @return true if the Jar loader is supported on the Java runtime
     */
    public static synchronized boolean isSupported() {
        try {
            return inst != null || getAddUrlMethod() != null;
        } catch (Throwable t) {
        }
        return false;
    }

    /**
     * Returns a string that describes the strategy being used to add JAR files
     * to the class path. This is meant mainly to assist with debugging and
     * diagnosing client issues.
     *
     * @return returns {@code "none"} if no strategy was found, otherwise a
     * short describing the method used; the value {@code "reflection"}
     * indicates that a fallback not compatible with Java 9+ is being used
     */
    public static synchronized String getStrategy() {
        String strat = "none";
        if (inst != null) {
            strat = loadedViaPreMain ? "agent" : "agent (main)";
        } else {
            try {
                if (isSupported()) {
                    strat = "reflection";
                }
            } catch (Throwable t) {
            }
        }
        return strat;
    }

    /**
     * Called by the JRE. <em>Do not call this method from user code.</em>
     *
     * <p>
     * This method is automatically invoked when the JRE loads this class as an
     * agent using the option {@code -javaagent:jarPathOfThisClass}.
     *
     * <p>
     * For this to work the {@code MANIFEST.MF} file <strong>must</strong>
     * include the line {@code Premain-Class: ca.cgjennings.jvm.JarLoader}.
     *
     * @param agentArgs       agent arguments; currently ignored
     * @param instrumentation provided by the JRE
     */
    public static void premain(String agentArgs, Instrumentation instrumentation) {
        loadedViaPreMain = true;
        agentmain(agentArgs, instrumentation);
    }

    /**
     * Called by the JRE. <em>Do not call this method from user code.</em>
     *
     * <p>
     * This method is called when the agent is attached to a running process. In
     * practice, this is not how JarLoader is used, but it is implemented should
     * you need it.
     *
     * <p>
     * For this to work the {@code MANIFEST.MF} file <strong>must</strong>
     * include the line {@code Agent-Class: ca.cgjennings.jvm.JarLoader}.
     *
     * @param agentArgs       agent arguments; currently ignored
     * @param instrumentation provided by the JRE
     */
    public static void agentmain(String agentArgs, Instrumentation instrumentation) {
        if (instrumentation == null) {
            throw new NullPointerException("instrumentation");
        }
        if (inst == null) {
            inst = instrumentation;
        }
    }

    private static Instrumentation inst;

    private static Method getAddUrlMethod() {
        if (addUrlMethod == null) {
            addUrlThis = ClassLoader.getSystemClassLoader();
            if (addUrlThis instanceof URLClassLoader) {
                try {
                    final Method method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
                    method.setAccessible(true);
                    addUrlMethod = method;
                } catch (NoSuchMethodException nsm) {
                    throw new AssertionError(); // violates URLClassLoader API!
                }
            } else {
                throw new UnsupportedOperationException(
                        "did you forget -javaagent:<jarpath>?"
                );
            }
        }
        return addUrlMethod;
    }

    private static ClassLoader addUrlThis;
    private static Method addUrlMethod;
    private static boolean loadedViaPreMain = false;

    public static IntegrationManager getInstance() {
        return instance;
    }

    public void unload() {
        this.integrations.forEach(Integration::unload);
    }

    public void update(IntegrationStatus status) {
        this.integrations.forEach(x -> x.update(status));
    }
}