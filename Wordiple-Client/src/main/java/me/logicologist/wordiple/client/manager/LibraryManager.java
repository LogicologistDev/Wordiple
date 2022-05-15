package me.logicologist.wordiple.client.manager;

import me.logicologist.wordiple.client.WordipleClient;
import me.logicologist.wordiple.common.utils.Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.jar.JarFile;

public class LibraryManager {

    public enum Libraries {

        DISCORD_PRESENCE("https://www.dropbox.com/s/21y28ul3m2d0xzf/discord-rpc.jar?dl=1", "discord-rpc.jar"),
        UPDATER("", "Updater.jar");

        private final String url;
        private final File file;

        Libraries(String url, String fileName) {
            this.url = url;
            this.file = fileName == null ? null : new File(WordipleClient.getAppData() + File.separator + "libraries", fileName);
            if (file != null && !file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
        }

        public String getUrl() {
            return url;
        }

        public File getFile() {
            return this.file;
        }

        public boolean needDownload(String version, String localVersion) {
            if (file == null) return false;

            boolean exists = file.exists();
            return !exists || !version.equals(localVersion);
        }

        public void download(Runnable assetsInsert) {
            if (this.url == null) {
                return;
            }
            try {
                if (!file.exists()) {
                    file.createNewFile();
                }
                HttpURLConnection httpcon = (HttpURLConnection) new URL(url).openConnection();
                httpcon.addRequestProperty("User-Agent", "Mozilla/4.0");
                httpcon.setConnectTimeout(5 * 1000);
                httpcon.setReadTimeout(5 * 1000);
                WordipleClient.getLogger().info("Downloaded library " + this.file);
                Files.copy(httpcon.getInputStream(), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            assetsInsert.run();
        }
    }

    private static LibraryManager instance;

    private final String version;
    private final String localVersion;

    public LibraryManager() {
        instance = this;
        this.version = Utils.getAssetVersion();
        this.localVersion = SessionManager.getInstance().getLocalAssetVersion();
    }

    public static LibraryManager getInstance() {
        return instance;
    }

    public void load() {
        for (Libraries value : Libraries.values()) {
            try {
                if (!value.needDownload(version, localVersion)) {
                    addToClassPath(value.getFile());
                    continue;
                }
                value.download(GenericManager.assetsInsert);
                addToClassPath(value.getFile());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        WordipleClient.getLogger().info("Libraries successfully loaded");
    }

    public long neededDownloaded() {
        return Arrays.stream(Libraries.values()).filter(x -> x.needDownload(version, localVersion)).count();
    }

    /**
     * Adds a JAR file to the list of JAR files searched by the system class
     * loader. This effectively adds a new JAR to the class path.
     *
     * @param jarFile the JAR file to add
     * @throws IOException if there is an error accessing the JAR file
     */
    public static synchronized void addToClassPath(File jarFile) throws IOException {
        WordipleClient.getLogger().info("Loading integration plugin: " + jarFile);
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
        } catch (UnsupportedOperationException e) {
            WordipleClient.getLogger().info("Running jar in IDE mode: " + jarFile);
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
}
