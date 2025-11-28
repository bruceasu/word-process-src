package me.asu.word.hyly_single;


import me.asu.util.Streams;
import me.asu.word.Tags;
import me.asu.word.Word;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.*;

import static me.asu.word.ResourcesFiles.loadWords;
import static me.asu.word.ResourcesFiles.readLinesInResources;

/**
 * 主打单字
 */
public class HylySingle {

    public static void main(String[] args) throws IOException {
        String name = "s";
//        Map<String, List<String>> he     = loadAsMapList("he.txt");
//        Map<String, List<String>> xm      = loadAsMapList("rain.txt");
//
//        List<Word>                merged  = Merge.merge(he, xm);
//        List<Word> merged = loadWords("a.txt", true);
        List<Word> merged = loadWords("merged-he-s.txt", true);
        List<String> oneSet = readLinesInResources("he_1_2.txt");
        List<String> k = readLinesInResources("k.txt");
        Map<String, Set<String>> symbols = new HashMap<>();
        for (String s : k) {
            String[] split = s.split("\\s+");
            if (split.length == 2) {
                Set<String> strings = symbols.computeIfAbsent(split[0], key -> new HashSet<>());
                strings.add(split[1]);
            } else {
                System.out.println("Error: " + s);
            }
        }
        // add tones to word
        List<Word> newList = new ArrayList<>(merged.size() * 2);
        for (Word word : merged) {
            String w = word.getWord();
            if (symbols.containsKey(w)) {
                Set<String> set = symbols.get(w);
                String code = word.getCode().substring(0, 2);
                boolean found = false;
                for (String s : set) {
                    if (s.startsWith(code)) {
                        Word clone = word.clone();
                        clone.setCode(s + word.getCode().substring(2));
                        newList.add(clone);
                        found = true;
                    }
                }
                if (!found) {
                    System.out.println("Couldn't find the tones of word: " + word.toString());
                    //newList.add(word);
                }
            } else {
                System.out.println("Couldn't find the tones of word: " + word.toString());
                //newList.add(word);
            }

        }
//        List<String> oneSet = new ArrayList<>();
        Tags.tag(newList);
        Map<String, List<Word>> results = new MergedMakeShortSingle().makeSort(newList, oneSet);

        save(name, results);
    }

    protected static void save(String name, Map<String, List<Word>> results)
    throws IOException {
        File outdir = new File("out");
        if (!outdir.exists()) {
            outdir.mkdirs();
        }
        File outFile = new File(outdir, name + "-out.txt");
        List<Word> result = results.get("result");

        writeTo(result, outFile, 60000);
        System.out.printf("保存到： %s, %d lines%n", outFile.getAbsolutePath(),
                result.size());

        File outFile2 = new File(outdir, name + "-out2.txt");
        List<Word> result2 = results.get("result2");
        writeTo(result2, outFile2, 50000);
        System.out.printf("保存到： %s, %d lines%n", outFile2.getAbsolutePath(),
                result2.size());

        File outFile3 = new File(outdir, name + "-out3.txt");
        List<Word> result3 = results.get("result3");
        writeTo(result3, outFile3, 40000);
        System.out.printf("保存到： %s, %d lines%n", outFile3.getAbsolutePath(),
                result3.size());

        File outFile4 = new File(outdir, name + "-out4.txt");
        List<Word> result4 = results.get("result4");
        writeTo(result4, outFile4, 30000);
        System.out.printf("保存到： %s, %d lines%n", outFile4.getAbsolutePath(),
                result4.size());

        File outFile5 = new File(outdir, name + "-out5.txt");
        List<Word> result5 = results.get("result5");
        writeTo(result5, outFile5, 20000);
        System.out.printf("保存到： %s, %d lines%n", outFile5.getAbsolutePath(),
                result5.size());

        File outFile6 = new File(outdir, name + "-out6.txt");
        List<Word> result6 = results.get("result6");
        writeTo(result6, outFile6, 10000);
        System.out.printf("保存到： %s, %d lines%n", outFile6.getAbsolutePath(),
                result6.size());

        File outFile7 = new File(outdir, name + "-out7.txt");
        List<Word> result7 = results.get("result7");
        writeTo(result7, outFile7, 5000);
        System.out.printf("保存到： %s, %d lines%n", outFile7.getAbsolutePath(),
                result7.size());

        File outFullFile = new File(outdir, name + "-full.txt");
        List<Word> full = results.get("full");
        writeFullTo(full, outFullFile, 15000);
        System.out.printf("保存到： %s, %d lines%n", outFullFile.getAbsolutePath(),
                full.size());
    }

    protected static void writeTo(List<Word> list, File outFile, int startFreq)
    throws IOException {
        try (Writer writer = Streams.fileOutw(outFile)) {
            Collections.sort(list);
            for (int i = 0; i < list.size(); i++) {
                Word w = list.get(i);
                int freq = startFreq - i;
                String ext;
                ext = String.format("#序%d", freq);
                writer.write(
                        String.format("%s\t%s%s%n", w.getWord(), w.getCode(),
                                ext));
            }
        }
    }

    protected static void writeFullTo(List<Word> list, File outFile,
                                      int startFreq)
    throws IOException {
        try (Writer writer = Streams.fileOutw(outFile)) {
            Collections.sort(list);
            for (int i = 0; i < list.size(); i++) {
                Word w = list.get(i);
                int freq = startFreq - i;
                String ext = String.format("#序%d", freq);
                writer.write(
                        String.format("%s\t%s%s%n", w.getWord(), w.getCode(),
                                ext));
            }
        }
    }

    protected static void writeSpTo(List<Word> list, File outFile,
                                    int startFreq)
    throws IOException {
        try (Writer writer = Streams.fileOutw(outFile)) {
            Collections.sort(list);
            for (int i = 0; i < list.size(); i++) {
                Word w = list.get(i);
                int freq = startFreq - i;
                String ext = String.format("#序%d", freq);
                writer.write(
                        String.format("%s\t%s%s%n", w.getWord(), w.getCode(),
                                ext));
            }
        }
    }
}