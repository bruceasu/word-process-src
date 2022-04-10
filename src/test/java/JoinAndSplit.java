import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.Map.Entry;
import me.asu.util.Strings;

/**
 * Created by suk on 2019/7/14.
 */
public class JoinAndSplit {

    public static void main(String[] args) throws IOException {
        join("out/b.txt", "join.txt");
    }

    private static void join(String in, String out) throws IOException {
        List<String>              he      = Files.readAllLines(Paths.get(in));
        Path outPath = Paths.get("out", out);
        Map<String, List<String>> m = new TreeMap<>();
        for (String line : he) {
            if (Strings.isBlank(line)) { continue; }
            if (line.startsWith("#")) { continue; }

            String[] split = line.split("\\s+");
            String   hz   = split[0];
            String   code   = split[1];
            m.computeIfAbsent(code, k->new LinkedList<>());
            m.get(code).add(hz);
        }

        try (BufferedWriter writer = Files.newBufferedWriter(outPath)) {
            for (Entry<String, List<String>> entry : m.entrySet()) {
                String k = entry.getKey();
                List<String> vs = entry.getValue();
                writer.write(k);
                writer.write('\t');
                for (String v : vs) {
                    writer.write(v);
                    writer.write(' ');
                }
                writer.write('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("DONE");
    }


}
