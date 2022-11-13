package me.asu.word;

import static me.asu.cli.command.cnsort.Orders.searchSimplifiedOrder;
import static me.asu.word.ResourcesFiles.loadAsMapList;

import java.io.IOException;
import java.io.Writer;
import java.util.*;
import me.asu.util.Streams;

public class Merge {

    public static void main(String[] args) throws IOException {
        Map<String, List<String>> he = loadAsMapList("k.txt");
//        Map<String, List<String>> he = loadAsMapList("he-single.txt");
//        Map<String, List<String>> he = loadAsMapList("he-s-t.txt");
//        Map<String, List<String>> xm = loadAsMapList("kuaishou.txt");
//        Map<String, List<String>> xm = loadAsMapList("cs.txt");
//        Map<String, List<String>> xm = loadAsMapList("rain.txt");
//        Map<String, List<String>> xm = loadAsMapList("rain-s-t.txt");
        Map<String, List<String>> xm  = loadAsMapList("rain-s-t-all.txt");
        List<Word>                ret = merge(he, xm);

        Writer writer = Streams.fileOutw("out\\merged.txt");
        for (Word s : ret) {
            String codeExt = s.getCodeExt();
            writer.write(String.format("%s\t%s%s\n", s.getWord(), s.getCode(),
                    codeExt));
        }
        Streams.safeClose(writer);
    }

    public static List<Word> merge(Map<String, List<String>> first,
                                   Map<String, List<String>> last) {
        Objects.requireNonNull(first);
        Objects.requireNonNull(last);
        List<Word>   result   = new ArrayList<>();
        List<String> notMatch = new ArrayList<>();
        for (Map.Entry<String, List<String>> kv : first.entrySet()) {
            String       key  = kv.getKey();
            List<String> val  = kv.getValue();
            List<String> val2 = last.get(key);
            if (val2 == null) {
                notMatch.add(key);
                continue;
            }
            for (String s1 : val) {
                for (String s2 : val2) {
                    Word w = new Word();
                    w.setCode(s1);
                    w.setCodeExt(s2);
                    w.setWord(key);
                    w.setOrder(searchSimplifiedOrder(key));
                    result.add(w);
                }
            }

        }
        Collections.sort(result);
        System.out.println("Not match: " + notMatch.size());
        System.out.println("Not match: " + notMatch);
        return result;
    }
}
