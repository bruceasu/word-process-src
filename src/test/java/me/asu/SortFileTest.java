package me.asu;

import java.io.IOException;
import junit.framework.TestCase;

public class SortFileTest extends TestCase {
    public void testSort() throws IOException {
        SortFile.main(new String[]{
                "-i", "src/main/resources/merged-he-t.txt",
                "-o", "out/a.txt",
                "-e", "utf-8",
                "-t"
        });
    }
}