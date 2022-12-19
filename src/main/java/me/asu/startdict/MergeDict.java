package me.asu.startdict;

import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.Map.Entry;

public class MergeDict {

    public static void main(String[] args) throws IOException {
        if (args.length < 2) {
            usage();
            System.exit(1);
        }
        Path output = Paths.get(args[0]);
        if (!Files.exists(output.getParent())) {
            Files.createDirectories(output.getParent());
        }
        Map<String, Set<String>> data = new TreeMap<>();
        try(CsvWriter csvWriter = new CsvWriter(output, ',',
                StandardCharsets.UTF_8)) {
            for (int i = 1; i < args.length; i++) {
                System.out.println("Loading " + args[i]);
                CsvReader csvReader = new CsvReader(args[i], ',',
                        StandardCharsets.UTF_8);
                while(csvReader.readRecord()) {
                    String k = csvReader.get(0);
                    String v = csvReader.get(1);
                    if (data.containsKey(k)) {
                        continue;
                    }
                    data.putIfAbsent(k, new HashSet<>());
                    Set<String> list = data.get(k);
                    String[] values = v.split(";");
                    for (String value : values) {
                        if (value==null||value.trim().isEmpty()) continue;
                        list.add(value.replaceAll("【.+】","").trim());
                    }
                }
                System.out.println("Loaded " + args[i]);
            }

            for (Entry<String, Set<String>> entry : data.entrySet()) {
                String k = entry.getKey();
                Set<String> v = entry.getValue();
                StringBuilder b = new StringBuilder();
                for (String s : v) {
                    b.append(s).append(';');
                }
                if (b.length() > 0)
                    b.setLength(b.length() - 1);
                csvWriter.writeRecord(new String[]{k, b.toString()});
            }
            System.out.println("Done! Save to " + output);
        }
    }

    private static void usage() {
        System.out.println("MergeDict <output> input1 [input2, input3 ...]");
    }
}
