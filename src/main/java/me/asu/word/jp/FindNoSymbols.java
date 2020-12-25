package me.asu.word.jp;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FindNoSymbols
{

    public static void main(String[] args) throws IOException
    {
        String jp = "src/main/resources/日本常用汉字表.txt";
        String he = "src/main/resources/he-s-t.txt";
        String rain = "src/main/resources/rain-s-t.txt";
        String out = "out/jp.txt";
        String out2 = "out/jp-not.txt";

        Map<String, List<String>> hes = getHes(he);
        Map<String, List<String>> rains = getHes(rain);
        Map<String, List<String>> mapping = mapping(hes, rains);
        process(jp, out, out2, mapping);
        System.out.println("DONE!");
    }

    private static Map<String, List<String>> getHes(String file) throws IOException
    {
        List<String> lines = Files.readAllLines(Paths.get(file), StandardCharsets.UTF_8);
        Map<String, List<String>> hes = new HashMap<>();
        lines.forEach(s -> {
            if (s.startsWith("#") || s.trim().isEmpty()) {
                return;
            }
            String[] split = s.split("\\s+");
            String w = split[0], c = split[1];
            if (hes.containsKey(w)) {
                hes.get(w).add(c);
            } else {
                List<String> list = new ArrayList<>();
                list.add(c);
                hes.put(w, list);
            }
        });
        return hes;
    }

    private static Map<String, List<String>> mapping(Map<String, List<String>> hes,
                                                     Map<String, List<String>> rains)
    {
        Map<String, List<String>> mapping = new HashMap<>();
        hes.forEach((w, list)->{
            List<String> list2 = rains.get(w);
            if (list2 == null || list2.isEmpty()) {
                mapping.put(w, list);
            } else {
                List<String> list3 = new ArrayList<>();
                for (String h : list) {
                    for (String r : list2) {
                        list3.add(h+r);
                    }
                }
                mapping.put(w, list3);
            }
        });
        return mapping;
    }

    private static void process(String jp,
                                String out,
                                String out2,
                                Map<String, List<String>> mapping) throws IOException
    {
        List<String> lines = Files.readAllLines(Paths.get(jp), StandardCharsets.UTF_8);
        List<String> result = new ArrayList<>();
        List<String> result2 = new ArrayList<>();
        for (String line : lines) {
            if (mapping.containsKey(line)) {
                List<String> strings = mapping.get(line);
                for (String string : strings) {
                    result.add(line + "\t" + string);
                }
            } else {
                result2.add(line);
            }
        }

        Files.write(Paths.get(out), result);
        Files.write(Paths.get(out2), result2);
    }
}
