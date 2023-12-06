
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import me.asu.util.Strings;
import me.asu.word.ResourcesFiles;

import static me.asu.word.ResourcesFiles.*;

/**
 * Created by suk on 2019/7/14.
 */
public class Filter {

    public static void main(String[] args) throws IOException {
        find1();
 //       find2();
//       test();
    }

    private static void test() throws IOException {
        List<String> a = ResourcesFiles.readLinesInResources("out/a.txt");
        String b = "out\\b.txt";

        Map<String,Set<String>> single = new HashMap<>();
        final List<String> strings = readLinesInResources("he-single.txt");
        for (String line : strings) {
            if (Strings.isBlank(line)) {
                continue;
            }
            if (line.startsWith("#")) {
                continue;
            }

            String[] split = line.split("\\s+");
            if (single.containsKey(split[0])) {
                single.get(split[0]).add(split[1]);
            } else {
                final HashSet<String> set = new HashSet<>();
                set.add(split[1]);
                single.put(split[0], set);
            }
        }
        try(BufferedWriter writer = Files.newBufferedWriter(Paths.get(b), StandardCharsets.UTF_8)) {
            for(String line :a) {
                if (Strings.isBlank(line)) {
                    continue;
                }
                if (line.startsWith("#")) {
                    continue;
                }

                String[] split = line.split("\\s+");
                final Set<String> s = single.get(split[0].substring(split[0].length() - 1));
                if (s != null) {
                    for (String c : s) {
                        writer.write(line);
                        writer.write(c);
                        writer.write("#序25000");
                        writer.write('\n');
                    }

                }
            }
        }

        System.out.println("DONE");
    }

    private static void find1() throws IOException {
        Set<String> a = new HashSet<>(Files.readAllLines(Paths.get("out/a.txt")));
        Set<String> b = new HashSet<>(Files.readAllLines(Paths.get("out/b.txt")));
        b.removeAll(a);
        Path out = Paths.get("out", "c.txt");
        try (BufferedWriter writer = Files.newBufferedWriter(out)) {
            for (String line : b) {
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

    private static void find2() throws IOException {
        List<String> c = Files.readAllLines(Paths.get("src/main/resources/sort-order-cj.txt"));
        Map<String, String> map = loadAsMapIfAbsent("src/main/resources/cj-mapping.txt");
        Path out = Paths.get("out", "tmp-sort-order.txt");
        try (BufferedWriter writer = Files.newBufferedWriter(out)) {
            for (String line : c) {
                if (Strings.isBlank(line)) {
                    continue;
                }
                if (line.startsWith("#")) {
                    continue;
                }

                if (map.containsKey(line)) {
                    writer.write(map.get(line));
                    writer.write("\n");
                } else {
                    writer.write(line);
                    writer.write("\n");
                }
            }
        }
        System.out.println("DONE");
    }

}
