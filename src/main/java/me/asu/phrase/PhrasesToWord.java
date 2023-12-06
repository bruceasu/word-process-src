package me.asu.phrase;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class PhrasesToWord {
    public static void main(String[] args) throws IOException {
        String in = "d:/temp/带調詞.txt";
        String out = "out/a.txt";
        Map<String, Set> words= new HashMap<>();
        try(
            BufferedWriter writer=Files.newBufferedWriter(Paths.get(out), StandardCharsets.UTF_8)
        ) {
            List<String> lines = Files.readAllLines(Paths.get(in), StandardCharsets.UTF_8);
            for (String line : lines) {
                if (line == null || line.trim().isEmpty()) continue;
                if (line.startsWith("#")) {
                    System.out.println("Not process: "+ line);
                    continue;
                }

                String[] pv = line.split("\t");
                if (pv.length!= 2) {
                    System.out.println("Not process: "+ line);
                    continue;
                }
                List<String> wordList = phraseToWordList(pv[0]);

                String[] codes = pv[1].split(" ");
                if (codes.length != wordList.size()) {
                    System.out.println("Not process: "+ line);
                    continue;
                }

                mapping(words, wordList, codes);
            }
            for (Map.Entry<String, Set> entry : words.entrySet()) {
                String k = entry.getKey();
                Set v = entry.getValue();
                for (Object o : v) {
                    writer.write(k);
                    writer.write(' ');
                    writer.write(o.toString());
                    writer.write("\n");
                }
            }
        }
        System.out.println("Write to " + out);
        System.out.println("Done");
    }

    private static void mapping(Map<String, Set> words, List<String> wordList, String[] codes) {
        for (int i = 0; i < wordList.size(); i++) {
            String w = wordList.get(i);
            String c = codes[i];

            if (!words.containsKey(w)) {
                words.put(w, new TreeSet());
            }
            Set<String> set = words.computeIfAbsent(w, (k)-> new TreeSet());
            set.add(c);
        }
    }

    private static List<String> phraseToWordList(String s) {
        List<String> wordList = new ArrayList<>();
        char[] chars = s.toCharArray();
        for (int i = 0, charsLength = chars.length; i < charsLength; i++) {
            char c = chars[i];
            if (Character.isSurrogate(c)) {
                wordList.add(new String(new char[]{c, chars[i+i]}));
                i++;
            } else {
                wordList.add(new String(new char[]{c}));
            }
        }
        return wordList;
    }
}
