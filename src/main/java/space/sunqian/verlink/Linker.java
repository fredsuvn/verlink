package space.sunqian.verlink;

import java.nio.file.Path;

/**
 * To create link file for directory.
 *
 * @author sunqian
 */
public interface Linker {

    static Linker get() {
        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.contains("windows")) {
            return WindowsLinker.INST;
        }
        throw new UnsupportedOperationException("Linker is not supported on " + osName);
    }

    /**
     * Create a link file for the directory.
     *
     * @param link the link file to link the directory
     * @param dir  the directory to be linked
     * @param type the link type
     * @throws VerlinkException if any error occurs
     */
    void linkDir(Path link, Path dir, LinkType type) throws VerlinkException;

    // /**
    //  * Returns the type of the link file, or {@code null} if the path is not a link file.
    //  *
    //  * @param path the path of the link file
    //  * @return the type of the link file
    //  */
    // LinkType getLinkType(Path path);
}
