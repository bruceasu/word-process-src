package me.asu.word;

import static me.asu.word.ResourcesFiles.loadAsMapList;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by suk on 2019/7/14.
 */
public class CommonWordWithToneFilter {

    public static void main(String[] args) throws IOException {
        Map<String, List<String>> he =  loadAsMapList("he.txt");
        Map<String, List<String>> he2 =  loadAsMapList("带调拼音-he.txt");
        Map<String, List<String>> he3 = new HashMap<>();
        he.forEach((w, list) -> {
            if (!he2.containsKey(w)) {
                System.out.println(w + "\t" + list);
            } else {
                List<String> newList= new ArrayList<>(list.size());
                List<String> list2 = he2.get(w);
                for(String s1: list) {
                    for (String s2: list2) {
                        if (s2.startsWith(s1)) {
                            newList.add(s2);
                        }
                    }
                }
                he3.put(w, newList);
            }
        });
        Path out = Paths.get("out", "he3.txt");
        BufferedWriter writer = Files.newBufferedWriter(out);

        he3.forEach((w, list) -> {
            for(String c : list)
                try {
                    writer.write(String.format("%s\t%s%n", w, c));
                } catch (IOException e) {
                    e.printStackTrace();
                }
        });
        System.out.println("DONE");
    }
}
