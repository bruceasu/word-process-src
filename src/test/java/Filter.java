
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
        List<String>              he      = Files.readAllLines(Paths.get("out/merged.txt"));
        Map<String, List<String>> he2     = loadAsMapList("out/merged-1.txt");
        Set<String>               wordSet = new HashSet<>();
        he.forEach(line -> {
            if (Strings.isBlank(line)) { return; }
            if (line.startsWith("#")) { return; }

            String[] split = line.split("\\s+");
            wordSet.add(split[0]);
        });
        Path out = Paths.get("out", "he3.txt");
        try (BufferedWriter writer = Files.newBufferedWriter(out)) {
            he2.forEach((w, list) -> {
                if (wordSet.contains(w)) { return; }
                try {
                    for (String s : list) {
                        writer.write(String.format("%s\t%s%n", w, s));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
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
