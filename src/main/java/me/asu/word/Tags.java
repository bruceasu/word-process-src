package me.asu.word;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Tags {
    static Map<String, Set<String>> tags = new HashMap<>();

    static {
        tags.put("F500", ResourcesFiles.w500());
        tags.put("F2000", ResourcesFiles.w2000());
        tags.put("GB2312_1", ResourcesFiles.gb2312_1());
        tags.put("GB2312", ResourcesFiles.gb2312());
        tags.put("GB", ResourcesFiles.gb());
//        tags.put("BIG5", ResourcesFiles.big5());
        tags.put("JP_SPEC", ResourcesFiles.japanese_spec());
        tags.put("BIG5_COMMON", ResourcesFiles.big5_common());
        tags.put("BIG5_COMMON_S", ResourcesFiles.big5_common_s());
        tags.put("HK_ONLY", ResourcesFiles.big5_hkscs_only());

    }


    synchronized public static void tag(List<Word> words) {
        words.forEach(word -> {
            tags.forEach((key, value) -> {
                if (value.contains(word.word)) word.tags.add(key);
            });
            if (word.tags.contains("F500")) { // L1
                word.setLevel(0);
            } else if (word.tags.contains("F2000")) { // L1
                word.setLevel(1);
            } else if (word.tags.contains("GB2312_1")) { // L2
                word.setLevel(2);
            } else if (word.tags.contains("GB2312")) { // L3
                word.setLevel(3);
            } else if (word.tags.contains("GB") || word.tags.contains("BIG5_COMMON_S")) { // L4
                word.setLevel(4);
            } else if (word.tags.contains("JP_SPEC")) { // 非常用字L1
                word.setLevel(5);
            } else if (word.tags.contains("HK_ONLY")) { // 非常用字L2
                word.setLevel(6);
            } else if (word.tags.contains("BIG5_COMMON")) { // 非常用字L3
                word.setLevel(7);
            } else {
                word.setLevel(8); // // 非常用字L4
            }

        });
    }
}