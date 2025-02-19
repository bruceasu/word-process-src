package me.asu.phrase;

import me.asu.word.ResourcesFiles;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class SortPhrases {

    /*
        1. 加载连绵词库
        2. 加载词库
        3. 过滤词库
           - 连绵词保留
           - 最多2重
           - 更多的放在扩展
     */
    public static void main(String[] args) throws IOException {
        final Set<String> set = ResourcesFiles.readLinesAsSetInResources("out/a.txt");
        Map<String, String> phrases = ResourcesFiles.loadAsMap("out/c.txt");
        Map<String, List<String>> list = new HashMap<>();

        phrases.forEach((k, v) -> {
            list.computeIfAbsent(v, (key) -> new LinkedList<>()).add(k);
        });


        File outDir = new File("out");
        if (!outDir.exists()) {
            outDir.mkdirs();
        }
        File       out1 = new File("out", "b.txt");
        File       out2 = new File("out", "d.txt");
        try(FileWriter fw1  = new FileWriter(out1);
            FileWriter fw2  = new FileWriter(out2)) {
            for (Map.Entry<String, List<String>> entry : list.entrySet()) {
                String k = entry.getKey();
                List<String> v = entry.getValue();
                if (v.size() < 3) {
                    for (String s : v) {
                        fw1.write(s);
                        fw1.write('\t');
                        fw1.write(k);
                        fw1.write('\n');
                    }
                } else {
                    int i = 0;

                    for (; i < v.size(); i++) {
                        String s = v.get(i);
                        if (i < 2 || set.contains(s)) {
                            fw1.write(v.get(i));
                            fw1.write('\t');
                            fw1.write(k);
                            fw1.write('\n');
                        } else {
                            fw2.write(s);
                            fw2.write('\t');
                            fw2.write(k);
                            fw2.write('\n');
                        }
                    }
                }
            }

        }

        System.out.println("Done!");
    }

}
