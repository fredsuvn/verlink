package tests.verlink;

import org.junit.jupiter.api.Test;
import space.sunqian.verlink.LinkType;
import space.sunqian.verlink.Linker;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class VerlinkTest {

    @Test
    public void testLinker() throws Exception {
        Linker linker = Linker.get();
        URL url = ClassLoader.getSystemResource("tmp/dir/hello.txt");
        Path hello = Paths.get(url.toURI());
        System.out.println(Files.readString(hello));
        Path dir = hello.getParent();
        Path tmp = dir.getParent();
        Path dirSymbolic = tmp.resolve("dirSymbolic");
        Path dirHard = tmp.resolve("dirHard");
        Path dirJunction = tmp.resolve("dirJunction");
        Files.deleteIfExists(dirSymbolic);
        Files.deleteIfExists(dirHard);
        Files.deleteIfExists(dirJunction);
        linker.linkDir(dirHard, dir, LinkType.HARD);
        linker.linkDir(dirSymbolic, dir, LinkType.SYMBOLIC);
        linker.linkDir(dirJunction, dir, LinkType.JUNCTION);
        // assertEquals(LinkType.JUNCTION, linker.getLinkType(dirJunction));
    }
}
