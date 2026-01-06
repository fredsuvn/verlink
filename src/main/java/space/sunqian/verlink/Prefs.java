package space.sunqian.verlink;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * Preferences of verlink.
 */
public class Prefs {

    private static final Prefs INST = new Prefs();

    public static Prefs get() {
        return INST;
    }

    private final Path prefs = getPreferenceFile();
    private final Properties props = new Properties();

    private Prefs() {
        try {
            InputStream in = Files.newInputStream(prefs);
            props.load(in);
            in.close();
        } catch (IOException e) {
            throw new VerlinkException(e);
        }
    }

    private Path getPreferenceFile() {
        Path verlink = Paths.get(System.getProperty("user.home"), ".verlink");
        verlink.toFile().mkdirs();
        Path prefs = verlink.resolve("prefs.properties");
        try {
            return Files.createFile(prefs);
        } catch (FileAlreadyExistsException ignored) {
            return prefs;
        } catch (IOException e) {
            throw new VerlinkException(e);
        }
    }

    public String get(String key) {
        return props.getProperty(key);
    }

    public String get(String key, String defaultValue) {
        return props.getProperty(key, defaultValue);
    }

    public void set(String key, String value) {
        props.setProperty(key, value);
    }

    public void save() {
        try (OutputStream out = Files.newOutputStream(prefs)) {
            props.store(out, null);
        } catch (IOException e) {
            throw new VerlinkException(e);
        }
    }
}
