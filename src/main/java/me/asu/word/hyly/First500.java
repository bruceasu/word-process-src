package me.asu.word.hyly;

import me.asu.word.Word;

import java.io.IOException;
import java.util.*;

import static me.asu.word.ResourcesFiles.*;

public class First500 {
    public static void main(String[] args) throws IOException {
        List<Word> merged = loadWords("merged-he-s.txt", true);
        List<String> oneSet = readLinesInResources("he_1_500.txt");
        Set<String> hzSet = processOneSet(oneSet);
        Set<String> codeSet = processOneSetCode(oneSet);
        Set<String> first500w = w500();
        Set<String> avail = getAvailablePhrases();

        tryEncode(merged, hzSet, codeSet, first500w, avail);

        forceEncode(merged, hzSet, codeSet, avail);

        System.out.println("Not found short:");
        for (Word word : merged) {
            System.out.println(word);
        }

        System.out.println("Available phrases:");
        for (String word : avail) {
            System.out.println(word);
        }

        System.out.println("DONE!");
    }

    private static void tryEncode(List<Word> merged, Set<String> hzSet, Set<String> codeSet, Set<String> first500w, Set<String> avail) {
        System.out.println("try encode:");
        for (Iterator<Word> iterator = merged.iterator(); iterator.hasNext(); ) {
            Word word = iterator.next();
            String w = word.getWord();
            if (hzSet.contains(w)) {
                iterator.remove();
                continue;
            }
            ;
            if (!first500w.contains(w)) {
                iterator.remove();
                continue;
            }
            ;
            String code = word.getCode();
            String c1 = code.substring(0, 1);
            String c2 = code.substring(0, 2);
            String c3 = code.substring(0, 3);

            String c4 = code.substring(0, 1) + code.substring(2, 3);
            String c5 = code.substring(0, 1) + code.substring(3, 4);
            String c6 = code.substring(2, 4);

            String[] ls1 = {c1, c2, c3};
            boolean found = false;
            for (String s : ls1) {
                if (!codeSet.contains(s)) {
                    System.out.printf("%s\t%s%n", w, s);
                    codeSet.add(s);
                    hzSet.add(w);
                    found = true;
                    break;
                }
            }

            if (found) {
                iterator.remove();
                continue;
            }

            String[] ls2 = {c4, c5, c6};
            for (String s : ls2) {
                if (!codeSet.contains(s)) {
                    System.out.printf("%s\t%s%n", w, s);
                    codeSet.add(s);
                    hzSet.add(w);
                    avail.remove(s);
                    found = true;
                    break;
                }
            }

            if (found) {
                iterator.remove();
                continue;
            }

        }
    }

    private static void forceEncode(List<Word> merged, Set<String> hzSet, Set<String> codeSet, Set<String> avail) {
        System.out.println("force encode:");
        for (Iterator<Word> iterator = merged.iterator(); iterator.hasNext(); ) {
            Word word = iterator.next();
            String w = word.getWord();
            String code = word.getCode();
            String c1 = code.substring(0, 1);
            for (String s : new HashSet<>(avail)) {
                if (!s.startsWith(c1)) continue;
                if (!codeSet.contains(s)) {
                    System.out.printf("%s\t%s%n", w, s);
                    codeSet.add(s);
                    hzSet.add(w);
                    avail.remove(s);
                    iterator.remove();
                    break;
                }
            }
        }
    }

    private static Set<String> getAvailablePhrases() {
        Set<String> phrases = readLinesAsSetInResources("he-syllable.txt");
        Set<String> avail = new TreeSet<>();
        for (int i = 'a'; i <= 'z'; i++) {
            for (int j = 'a'; j <= 'z'; j++) {
                String code = new String(new char[]{((char) i), ((char) j)});
                if (!phrases.contains(code)) avail.add(code);
            }
        }
        return avail;
    }

    protected static Set<String> processOneSet(List<String> oneSet) {
        if (oneSet == null || oneSet.isEmpty()) return Collections.emptySet();
        Set<String> hz = new HashSet<>();
        for (String str : oneSet) {
            String[] kv = str.split("\\s+");
            if (kv.length < 2) {
                continue;
            }


            if (kv[0].length() == 1) {
                hz.add(kv[0]);
            }
        }
        return hz;
    }


    protected static Set<String> processOneSetCode(List<String> oneSet) {
        if (oneSet == null || oneSet.isEmpty()) return Collections.emptySet();
        Set<String> codeSet = new HashSet<>();
        for (String str : oneSet) {
            String[] kv = str.split("\\s+");
            if (kv.length < 2) {
                continue;
            }


            if (kv[0].length() == 1) {
                codeSet.add(kv[1]);
            }
        }
        return codeSet;
    }
}
