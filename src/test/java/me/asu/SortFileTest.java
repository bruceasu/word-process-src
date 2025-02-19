package me.asu;

import java.io.IOException;
import junit.framework.TestCase;

public class SortFileTest extends TestCase {
    public void testSortFileWIthFreq() throws IOException {
        SortFileWithFreq.main(new String[]{
//                "-i", "src/main/resources/merged-j-cn.txt",
//                "-i", "src/main/resources/cantonese-3-spell.txt",
//                "-i", "src/main/resources/merged-cantonese-j.txt",
                "-i", "out/b.txt",
                "-o", "out/c.txt",
                "-e", "utf-8",
//                "-e", "GB18030",
                "-m",
//                "-t",
//                "-p",
//                "-j"
        });
    }
}