package me.asu;

import java.io.IOException;
import junit.framework.TestCase;

public class SortFileTest extends TestCase {
    public void testSort() throws IOException {
        SortFile.main(new String[]{
//                "-i", "src/main/resources/merged-j-cn.txt",
//                "-i", "src/main/resources/cantonese-3-spell.txt",
                "-i", "src/main/resources/merged-cantonese-j.txt",
                "-o", "out/merged3.txt",
                "-e", "utf-8",
//                "-e", "GB18030",
//                "-m",
//                "-t",
//                "-p",
//                "-j"
        });
    }
}