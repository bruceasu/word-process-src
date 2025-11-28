package me.asu.word;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Tags {
    static Map<String, Set<String>> tags = new HashMap<>();

    static {
        tags.put("F500", ResourcesFiles.w500());
        tags.put("GB2312_1", ResourcesFiles.gb2312_1());
        tags.put("GB2312", ResourcesFiles.gb2312());
        tags.put("GB", ResourcesFiles.gb());
//        tags.put("BIG5", ResourcesFiles.big5());
        tags.put("BIG5_COMMON", ResourcesFiles.big5_common());
        tags.put("BIG5_COMMON_S", ResourcesFiles.big5_common_s());
        tags.put("HK_ONLY", ResourcesFiles.big5_hkscs_only());
        tags.get("HK_ONLY").addAll(ResourcesFiles.japanese_spec());

    }


    synchronized public static void tag(List<Word> words) {
        words.forEach(word -> {
            tags.forEach((key, value) -> {
                if (value.contains(word.word)) word.tags.add(key);
            });
            if (word.tags.contains("F500")
                    || (word.tags.contains("GB2312_1") && word.tags.contains("HK_ONLY") && word.tags.contains("BIG5_COMMON_S"))
            ) {
                word.setLevel(0);
            } else if (word.tags.contains("GB2312_1") && word.tags.contains("BIG5_COMMON_S")) {
                word.setLevel(1);
            } else if (word.tags.contains("GB2312_1")) {
                word.setLevel(2);
            } else if (word.tags.contains("GB2312")) {
                word.setLevel(3);
            } else if (word.tags.contains("HK_ONLY")) {
                word.setLevel(4);
            } else if (word.tags.contains("GB")) {
                word.setLevel(5);
            } else if (word.tags.contains("BIG5_COMMON_S")) {
                word.setLevel(6);
            } else if (word.tags.contains("BIG5_COMMON")) {
                word.setLevel(7);
            } else {
                word.setLevel(8);
            }

        });
    }
}