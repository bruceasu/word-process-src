package me.asu.word;

import me.asu.util.Strings;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MakeRe {
    public static void main(String[] args) throws IOException {
        String in = "out/a.txt";
        String base = "src/main/resources/he-s-t.txt";
        String out = "out/b.txt";

        final List<String> strings = Files.readAllLines(Paths.get(in), StandardCharsets.UTF_8);
        Map<String, String> map = new HashMap<>();
        for (String line : strings) {
            if (Strings.isBlank(line)) continue;
            if (line.startsWith("#")) continue;
            final String[] split = line.split("\t");
            if (split.length != 2) continue;
            map.putIfAbsent(split[0], split[1]);
        }

        List<String> lines = Files.readAllLines(Paths.get(base), StandardCharsets.UTF_8);
        String pattern = "$ddcmd(%s,%s[%s])\t%s\n";
        try(BufferedWriter writer = Files.newBufferedWriter(Paths.get(out), StandardCharsets.UTF_8)) {
            for (String line : lines) {
                if (Strings.isBlank(line)) continue;
                if (line.startsWith("#")) continue;
                final String[] split = line.split("\t");
                if (split.length != 2) continue;
                if (map.containsKey(split[0])) {
                    String code = map.get(split[0]);
                    String newString = String.format(pattern, split[0], split[0], code, split[1]);
                    writer.write(newString);
                } else {
                    writer.write(line);
                    writer.write("\n");
                }
            }
        }

        System.out.println("Done!");
        System.out.println("Writer to " + out);
    }
}
