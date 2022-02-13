package me.asu;

import java.io.IOException;
import junit.framework.TestCase;

public class SortFileTest extends TestCase {
    public void testSort() throws IOException {
        SortFile.main(new String[]{
                "-i", "src/main/resources/qibu-ht.txt",
                "-o", "src/main/resources/qibu-ht2.txt",
                "-e", "utf-8",
        });
    }
}