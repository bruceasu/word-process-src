package me.asu.phrase;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import me.asu.util.Files;
import me.asu.word.ResourcesFiles;

/**
 * @author suk
 *
 * P2=L1B1+L1B2+L2B1+L2B2
 * P3=L1B1+L2B1+L3B1+L3B2
 * P>4=L1B1+L2B1+L3B1+L4B1
 */
public class CompilePhrases {

    public static void main(String[] args) throws IOException {
        File input = new File("/run/media/suk/Backup","k.txt");
        File output = new File("/run/media/suk/Backup", "kk.txt");

        List<String> list = Files.readLines(input);
        try (FileWriter fw = new FileWriter(output)) {
            for (String s : list) {
	            if (s == null || s.trim().isEmpty() || s.startsWith("#")) {
		            continue;
	            }

                String[] split = s.split("\\s+");

                if (split.length == 1) {
                    continue;
                }

                String w = split[0];
                String code = "";
                int len = w.length();
                if (len == 1 || len != split.length-1) {
                    System.out.println(s);
                    continue;
                }
                String c1 = split[1];
                String c2 = len>1 ? split[2] : "";
                String c3 = len > 2 ? split[3] : "";
                String cn = len > 3? split[len - 1] : "";

                switch (len) {
                    case 2:
                        code = genPhrase2(c1, c2);
                        break;
                    case 3:
                        code = genPhrase3(c1, c2, c3);
                        break;
                    default:
                        code = genPhrase4(c1, c2, c3, cn);
                        break;
                }
                fw.write(String.format("%s\t%s%n", w, code));
            }


        }

        System.out.println("Done!");
    }

    private static String genPhrase4(String c1, String c2, String c3, String cn) {
        return getCode(c1, 1)
                + getCode(c2, 1)
                + getCode(c3, 1)
                + getCode(cn, 1);
    }

    private static String genPhrase3(String c1, String c2, String c3) {
        return getCode(c1, 1)
                + getCode(c2, 1)
                + getCode(c3, 2);
    }

    private static String genPhrase2(String c1, String c2) {
        return getCode(c1, 2) + getCode(c2, 2);
    }

    private static String getCode(String code, int length) {
		return code.substring(0, length);
	}

}
