package me.asu.word;

import static me.asu.cli.command.cnsort.Orders.searchSimplifiedOrder;
import static me.asu.cli.command.cnsort.Orders.searchTraditionalOrder;

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

    public static Set<String> big5_hkscs() {
        return new HashSet<>(ResourcesFiles.readLinesInResources("big5-hkscs.txt"));
    }
    public static Set<String> big5() {
        return new HashSet<>(ResourcesFiles.readLinesInResources("big5.txt"));
    }
    public static Set<String> big5_common() {
        return new HashSet<>(ResourcesFiles.readLinesInResources("big5-common.txt"));
    }
    public static Set<String> japanese() {
        return new HashSet<>(ResourcesFiles.readLinesInResources("日本汉字水准1和水准2.txt"));
    }
    public static Set<String> generalSpecification() {
        return new HashSet<>(ResourcesFiles.readLinesInResources("通用规范字.txt"));
    }
    public static List<Word> loadWords(String file, boolean simplized) {
        List<String> strings = readLinesInResources(file);
        List<Word> list = new ArrayList<>(strings.size());
        for (String s : strings) {
            if (s == null) { continue; }
            s = s.trim();
            if (s.startsWith("#")) { continue; }
            if (s.isEmpty()) { continue; }
            String[] kv = s.split("\\s+");
            Word w = new Word();
            w.setCode(kv[1]);
            w.setCodeExt("");
            w.setWord(kv[0]);
            if(simplized) {
                w.setOrder(searchSimplifiedOrder(kv[0]));
            } else {
                w.setOrder(searchTraditionalOrder(kv[0]));
            }
            list.add(w);
        }
        return list;
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
