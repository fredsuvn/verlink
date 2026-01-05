package space.sunqian.verlink;

import java.nio.file.Path;

/**
 * Windows linker implementation.
 */
enum WindowsLinker implements Linker {
    INST;

    @Override
    public void linkDir(Path link, Path dir, LinkType type) throws VerlinkException {
        try {
            // if (LinkType.SYMBOLIC.equals(type)) {
            //     Files.createSymbolicLink(link, dir);
            //     return;
            // }
            String linkArg = switch (type) {
                case SYMBOLIC -> "/D";
                case HARD -> "/H";
                case JUNCTION -> "/J";
            };
            new ProcessBuilder("cmd", "/c",
                "mklink", linkArg, "\"" + link.toString() + "\"", "\"" + dir.toString() + "\"")
                .start()
                .waitFor();
        } catch (Exception e) {
            throw new VerlinkException(e);
        }
    }

    // @Override
    // public LinkType getLinkType(Path path) {
    //     if (Files.isSymbolicLink(path)) {
    //         return LinkType.SYMBOLIC;
    //     }
    //     return null;
    // }
}