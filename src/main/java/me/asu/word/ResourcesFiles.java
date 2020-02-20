package me.asu.word;

import java.io.File;
import java.util.*;
import me.asu.util.io.Files;

/**
 * Created by suk on 2019/6/2.
 */
public class ResourcesFiles {

    public static Set<String> w500() {
        return new HashSet<>(ResourcesFiles.readLinesInResources("common-words-500.txt"));
    }

    public static Set<String> w1600() {
        return new HashSet<>(ResourcesFiles.readLinesInResources("common-words-1600.txt"));
    }

    public static Set<String> w3800() {
        return new HashSet<>(ResourcesFiles.readLinesInResources("common-words-gb2312-1.txt"));
    }

    public static Set<String> w5700() {
        return new HashSet<>(ResourcesFiles.readLinesInResources("common-words.txt"));
    }

    public static Set<String> gb2312() {
        return new HashSet<>(ResourcesFiles.readLinesInResources("common-words-gb2312.txt"));
    }

    public static Set<String> loadCommonWords500() {
        return new HashSet<>(readLinesInResources("commons-words-500.txt"));
    }

    public static Set<String> loadCommonWords1600() {
        return new HashSet<>(readLinesInResources("commons-words-1600.txt"));
    }


    public static Set<String> loadCommonWordsGB2312_1() {
        return new HashSet<>(readLinesInResources("commons-words-gb2312_1.txt"));
    }


    public static Set<String> loadCommonWords() {
        return new HashSet<>(readLinesInResources("commons-words.txt"));
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

    public static List<String> readLinesInResources(String name) {
        String myDIR = OsHelper.getMyDIR();
        File file = new File(myDIR, name);
        if (!file.exists()) {
            file = new File(myDIR, "resources" + File.separator + name);
        }
        return Files.readLines(file);
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
