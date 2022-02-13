
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import me.asu.util.Strings;

/**
 * Created by suk on 2019/7/14.
 */
public class Filter {

    public static void main(String[] args) throws IOException {
//        find1();
//        find2();
        merge();
    }

    private static void merge() throws IOException {
        List<String>              he      = Files.readAllLines(Paths.get("out/he3.txt"));
        Map<String, List<String>> xm     = loadAsMapList("src/main/resources/rain-s-t.txt");
        Path out = Paths.get("out", "he4.txt");
        try (BufferedWriter writer = Files.newBufferedWriter(out)) {
            for (String line : he) {
                if (Strings.isBlank(line)) { continue; }
                if (line.startsWith("#")) { continue; }

                String[] split = line.split("\\s+");
                String   hz   = split[0];
                if (xm.containsKey(hz)) {
                    writer.write(line);
                    writer.write(xm.get(hz).get(0));
                    writer.write("\n");
                } else {
                    System.out.println(line);
                }
            }
        }
        System.out.println("DONE");
    }

    private static void find1() throws IOException {
        List<String>              he      = Files.readAllLines(Paths.get("src/main/resources/he.txt"));
        Map<String, List<String>> he2     = loadAsMapList("src/main/resources/he-tone.txt");
        Path out = Paths.get("out", "he3.txt");
        try (BufferedWriter writer = Files.newBufferedWriter(out)) {
            for (String line : he) {
                if (Strings.isBlank(line)) { continue; }
                if (line.startsWith("#")) { continue; }

                String[] split = line.split("\\s+");
                if (!he2.containsKey(split[0])) {
                    writer.write(line);
                    writer.write("\n");
                }
            }
        }
        System.out.println("DONE");
    }

    private static void find2() throws IOException {
        List<String>              he      = Files.readAllLines(Paths.get("python-src/py.txt"));
        Map<String, List<String>> he2     = loadAsMapList("out/he3.txt");
        Path out = Paths.get("out", "he4.txt");
        try (BufferedWriter writer = Files.newBufferedWriter(out)) {
            for (String line : he) {
                if (Strings.isBlank(line)) { continue; }
                if (line.startsWith("#")) { continue; }

                String[] split = line.split("\\s+");
                if (he2.containsKey(split[0])) {
                    List<String> s = he2.get(split[0]);
                    if (s.size() == 1 && split.length == 2) {
                        writer.write("1 ");
                        writer.write(split[0]);
                        writer.write("\t");
                        writer.write(s.get(0));
                        writer.write(line.charAt(line.length()-1));
                        writer.write("\n");
                    } else {
                        writer.write("2 ");
                        writer.write(split[0]);
                        writer.write("\t");
                        for (String l : s) {
                            writer.write(l);
                            writer.write(" ");
                        }
                        writer.write(line);
                        writer.write("\n");
                    }

                }
            }
        }
        System.out.println("DONE");
    }

    private static Map<String, List<String>> loadAsMapList(String s) throws IOException {
        List<String>        lines = Files.readAllLines(Paths.get(s));
        Map<String, List<String>> map = new HashMap<>();
        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty()) {
                continue;
            }
            String[] split = line.split("\\s+");
            if (split.length < 2) {
                continue;
            }
            if (map.containsKey(split[0])) {
                map.get(split[0]).add(split[1]);
            } else {
                List<String> ls = new ArrayList<>();
                ls.add(split[1]);
                map.put(split[0], ls);
            }
        }
        return map;
    }
}
