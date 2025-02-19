
import me.asu.util.Strings;
import me.asu.word.ResourcesFiles;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static me.asu.word.ResourcesFiles.readLinesAsSetInResources;
import static me.asu.word.ResourcesFiles.readLinesInResources;

/**
 * Created by suk on 2019/7/14.
 */
public class Filter {

    public static void main(String[] args) throws IOException {
        find3();
//        find2();
//        commonAndExtension();
    }

    private static void commonAndExtension() throws IOException {
        List<String>      total   = ResourcesFiles.readLinesInResources("/run/media/suk/Backup/03_projects/suk/my-sp-table/dd/he_total_phrases.txt", "UTF-16LE");
        String                   a       = "/run/media/suk/Backup/03_projects/suk/my-sp-table/dd/a.txt";
        String                   b       = "/run/media/suk/Backup/03_projects/suk/my-sp-table/dd/b.txt";
        String                   base    = "/run/media/suk/Backup/03_projects/suk/my-sp-table/dd/base.txt";
        Set<String>              baseSet     = readLinesAsSetInResources(base);
        Map<String, Set<String>> single  = new HashMap<>();

        try(BufferedWriter a_writer = Files.newBufferedWriter(Paths.get(a), StandardCharsets.UTF_8);
            BufferedWriter b_writer = Files.newBufferedWriter(Paths.get(b), StandardCharsets.UTF_8);
        ) {
            for(String line : total) {
                if (Strings.isBlank(line)) {
                    continue;
                }
                if (line.startsWith("#")) {
                    continue;
                }

                String[] split = line.split("\\s+");
                if (baseSet.contains(split[0])) {
                    a_writer.write(line);
                    a_writer.newLine();
                } else {
                    b_writer.write(line);
                    b_writer.newLine();
                }
            }
        }

        System.out.println("DONE");
    }

    // 无简体的字的繁体字符集
    private static void find1() throws IOException {
        final List<String>  aL      = Files.readAllLines(Paths.get("out/a.txt"));
        final List<String>  bL      = Files.readAllLines(Paths.get("out/b.txt"));
        Map<String, String> mapping = new HashMap<>();
        if (aL.size() != bL.size()) throw new RuntimeException("Can't mapping.");
        for (int i = 0; i < aL.size(); i++) {
            String x1 = aL.get(i);
            String y1 = bL.get(i);
            mapping.put(x1, y1);
        }
        Set<String> a = new HashSet<>(aL);
        Set<String> b = new HashSet<>(bL);
        Set<String> c = new HashSet<>(Files.readAllLines(Paths.get("src/main/resources/common-words-gb2312-1.txt")));

        a.retainAll(c);
        a.forEach(x -> mapping.remove(x));
        final Collection<String> values = mapping.values();
        b.retainAll(a);
        Path out  = Paths.get("out", "c.txt");
        Path out2 = Paths.get("out", "c2.txt");
        try (BufferedWriter writer = Files.newBufferedWriter(out2)) {
            for (String line : a) {
                writer.write(line);
                writer.write("\n");
            }
        }
        try (BufferedWriter writer = Files.newBufferedWriter(out)) {
            for (String line : values) {
                if (Strings.isBlank(line)) {
                    continue;
                }
                if (line.startsWith("#")) {
                    continue;
                }

                writer.write(line);
                writer.write("\n");
            }
        }
        System.out.println("DONE");
    }

    // 跟简体中文相同的繁体字集
    private static void find2() throws IOException {
//        Set<String> a = new HashSet<>(Files.readAllLines(Paths.get("src/main/resources/big5-common.txt")));
//        Set<String> b = new HashSet<>(Files.readAllLines(Paths.get("src/main/resources/通用规范字.txt")));
        Set<String> a = new HashSet<>(Files.readAllLines(Paths.get("src/main/resources/日本常用汉字表.txt")));
        Set<String> b = new HashSet<>(Files.readAllLines(Paths.get("out/c.txt")));

//        b.removeAll(a);
        Path out  = Paths.get("out", "a.txt");
        Path out2 = Paths.get("out", "b.txt");
        try (BufferedWriter writer = Files.newBufferedWriter(out);
             BufferedWriter writer2 = Files.newBufferedWriter(out2);
        ) {
            for (String line : a) {
                if (Strings.isBlank(line)) {
                    continue;
                }
                if (line.startsWith("#")) {
                    continue;
                }
                if (b.contains(line)) {
                    writer.write(line);
                    writer.newLine();
                } else {
                    writer2.write(line);
                    writer2.newLine();
                }
            }
        }
        System.out.println("DONE");
    }
    private static void find3() throws IOException {
        Set<String> a = new HashSet<>(Files.readAllLines(Paths.get("out/a.txt")));
        final Map<String, String> strings = ResourcesFiles.loadAsMap("out/b.txt");
        Path out1  = Paths.get("out", "he-out3.txt");
        Path out2  = Paths.get("out", "he-out4.txt");
        int c1 = 0, c2 = 0;
        try (BufferedWriter writer1 = Files.newBufferedWriter(out1);
             BufferedWriter writer2 = Files.newBufferedWriter(out2)
        ) {
            for (Map.Entry<String, String> entry : strings.entrySet()) {
                String k = entry.getKey();
                String v = entry.getValue();
                final String[] kv = v.split("#");
                System.out.println("k: " + k + " v: " + v + " kv[0]: " + kv[0]);
                if (a.contains(kv[0])) {
                    writer2.write(k);
                    writer2.write('\t');
                    writer2.write(v);
                    writer2.newLine();
                    c1++;
                } else {
                    a.add(kv[0]);
                    writer1.write(k);
                    writer1.write('\t');
                    writer1.write(v);
                    writer1.newLine();
                    c2++;
                }
            }
        }
        System.out.println("C1: " + c1 + " c2: " + c2);
        System.out.println("DONE");
    }

}
