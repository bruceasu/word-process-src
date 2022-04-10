package me.asu.word;

import java.io.File;
import java.util.*;
import me.asu.util.Files;

/**
 * Created by suk on 2019/6/2.
 */
public class ResourcesFiles {
    public static List<String> readLinesInResources(String name) {
        String myDIR = OsHelper.getMyDIR();
        File file = new File(myDIR, name);
        if (!file.exists()) {
            file = new File(myDIR, "resources" + File.separator + name);
        }
        return Files.readLines(file);
    }

    public static Set<String> w500() {
        return new HashSet<>(ResourcesFiles.readLinesInResources("common-words-500.txt"));
    }

    public static Set<String> w1000() {
        return new HashSet<>(ResourcesFiles.readLinesInResources("common-words-1000.txt"));
    }

    public static Set<String> w2000() {
        return new HashSet<>(ResourcesFiles.readLinesInResources("common-words-2000.txt"));
    }

    public static Set<String> w4000() {
        return new HashSet<>(ResourcesFiles.readLinesInResources("common-words.txt"));
    }

    public static Set<String> gb2312() {
        return new HashSet<>(ResourcesFiles.readLinesInResources("common-words-gb2312.txt"));
    }


    public static Map<String, String> loadAsMap(String name) {
        List<String> strings = readLinesInResources(name);
        Map<String, String> map = new HashMap<>();
        for (String line : strings) {
            line = line.trim();
            if (line.isEmpty()) {
                continue;
            }
            String[] split = line.split("\\s+");
            if (split.length < 2) {
                continue;
            }
            map.put(split[0], split[1]);
        }
        return map;
    }

    public static Map<String, List<String>> loadAsMapList(String name) {
        List<String> strings = readLinesInResources(name);
        Map<String, List<String>> map = toMapList(strings);
        return map;
    }

    public static Map<String, List<String>> toMapList(List<String> strings) {
        Map<String, List<String>> map = new HashMap<>();
        for (String line : strings) {
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

    public static Map<String, List<String>> loadBuShou() {
        List<String> strings = readLinesInResources("bushou.txt");
        Map<String, List<String>> map = new HashMap<>();
        for (String line : strings) {
            line = line.trim();
            if (line.isEmpty()) {
                continue;
            }
            String[] split = line.split("\\s+");
            if (split.length < 2) {
                continue;
            }
            String code = split[0];
            for (int i = 1; i < split.length; i++) {
                String w = split[i];
                if (map.containsKey(w)) {
                    map.get(w).add(code);
                } else {
                    List<String> ls = new ArrayList<>();
                    ls.add(code);
                    map.put(w, ls);
                }
            }

        }
        return map;
    }

    public static List<String> readLinesInResources(String name, String charset) {
        String myDIR = OsHelper.getMyDIR();
        File file = new File(myDIR, name);
        if (!file.exists()) {
            file = new File(myDIR, "resources" + File.separator + name);
        }
        return Files.readLines(file, charset);
    }
}
