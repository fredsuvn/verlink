package space.sunqian.verlink;

import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Directory info: parent directory, its subdirectories and selected directory.
 *
 * @author sunqian
 */
public record Dirs(Path parentDir, List<Path> subDirs) {

    public Dirs(Path parentDir) {
        // this.parentDir = parentDir;
        File[] subFiles = parentDir.toFile().listFiles();
        List<Path> subDirs;
        if (subFiles != null) {
            subDirs = Arrays.stream(subFiles)
                .filter(File::isDirectory)
                .map(File::toPath)
                .toList();
        } else {
            subDirs = Collections.emptyList();
        }
        this(parentDir, subDirs);
    }
}
