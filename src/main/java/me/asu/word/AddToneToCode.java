package me.asu.word;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * Adds pronunciation tone numbers to the pinyin portion of encoded entries.
 */
public class AddToneToCode {
    private static final Pattern TONE_PINYIN = Pattern.compile("^(.+?)([1-5])$");

    public static void main(String[] args) throws IOException {
        if (args.length != 3) {
            System.err.println("Usage: AddToneToCode <tone-pinyin-file> <code-file> <output-file>");
            System.exit(1);
        }
        addTones(Paths.get(args[0]), Paths.get(args[1]), Paths.get(args[2]));
    }

    static void addTones(Path toneFile, Path codeFile, Path outputFile) throws IOException {
        Map<String, List<String>> tonesByWord = readEntries(toneFile);
        Path parent = outputFile.toAbsolutePath().getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }

        try (BufferedWriter writer = Files.newBufferedWriter(outputFile, StandardCharsets.UTF_8);
             Stream<String> lines = Files.lines(codeFile, StandardCharsets.UTF_8)) {
            for (String line : (Iterable<String>) lines::iterator) {
                String[] entry = splitEntry(line);
                if (entry == null) {
                    continue;
                }
                String word = entry[0];
                String code = entry[1];
                if (code.length() < 2) {
                    continue;
                }

                List<String> tones = tonesByWord.get(word);
                if (tones == null) {
                    continue;
                }
                for (String tone : tones) {
                    Matcher matcher = TONE_PINYIN.matcher(tone);
                    if (matcher.matches() && matcher.group(1).length() >= 2
                            && code.regionMatches(true, 0, matcher.group(1), 0, 2)) {
                        writer.write(word);
                        writer.write('\t');
                        writer.write(code.substring(0, 2));
                        writer.write(matcher.group(2));
                        writer.write(code.substring(2));
                        writer.newLine();
                    }
                }
            }
        }
    }

    private static Map<String, List<String>> readEntries(Path file) throws IOException {
        Map<String, List<String>> entries = new HashMap<>();
        try (Stream<String> lines = Files.lines(file, StandardCharsets.UTF_8)) {
            for (String line : (Iterable<String>) lines::iterator) {
                String[] entry = splitEntry(line);
                if (entry != null) {
                    entries.computeIfAbsent(entry[0], key -> new ArrayList<>()).add(entry[1]);
                }
            }
        }
        return entries;
    }

    private static String[] splitEntry(String line) {
        String trimmed = line.trim();
        if (trimmed.isEmpty() || trimmed.startsWith("#")) {
            return null;
        }
        String[] fields = trimmed.split("\\s+", 2);
        return fields.length == 2 ? fields : null;
    }
}