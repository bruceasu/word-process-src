package me.asu.word.wubi;


import static me.asu.cli.command.cnsort.Orders.searchSimplifiedOrder;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.extern.slf4j.Slf4j;
import me.asu.word.ResourcesFiles;
import me.asu.word.Word;

/**
 * Created by suk on 2019/6/4.
 */
@Slf4j
public class WubiMakeShort
{
    // group
    static List<Word> group500 = new ArrayList<>();
    static List<Word> group1600 = new ArrayList<>();
    static List<Word> group3800 = new ArrayList<>();
    static List<Word> group5700 = new ArrayList<>();
    static List<Word> groupOther = new ArrayList<>();

    static Set<String> fullCodes = new HashSet<>();
    static List<Word> result = new ArrayList<>();
    static List<Word> full = new ArrayList<>();
    static List<Word> sp = new ArrayList<>();
    static Map<String, AtomicInteger> codeSet = new HashMap<>();
    static int[] codeLenCounter = new int[30];
    private static String[] padding = new String[] {"", ";", "/", "/", "/", "/", "/", "/", "/", "/", "/"};

    static {
        for (int i = 0; i<30; i++) {
            codeLenCounter[i] = 0;
        }
    }

    public static Map<String, List<Word>> makeSort(List<String> lines, List<String> oneSet) throws IOException {

        Set<String> w500 = ResourcesFiles.w500();
        Set<String> w1600 = ResourcesFiles.w1000();
        Set<String> w3800 = ResourcesFiles.w2000();
        Set<String> w5700 = ResourcesFiles.w4000();
        Set<String> wGb2312 = ResourcesFiles.gb2312();
        Set<String> single = new HashSet<>();

        for (String str : oneSet) {
            String[] kv = str.split("\\s+");
            if(kv.length < 2) {
                continue;
            }
            if (codeSet.containsKey(kv[1])) {
                codeSet.get(kv[1]).incrementAndGet();
            } else {
                codeSet.put(kv[1], new AtomicInteger(1));
            }
            codeLenCounter[1] += 1;
            Word w = new Word();
            w.setWord(kv[0]);
            w.setCode(kv[1]);
            w.setLevel(1);
            w.setOrder(searchSimplifiedOrder(kv[0]));
            result.add(w);
            single.add(kv[0]);
        }

        for(String line: lines) {
            String trim = line.trim();
            if (trim.isEmpty() || trim.startsWith("#")) {
                continue;
            }
            String[] kv = trim.split("\\s+");
            if (kv.length < 2) {
                continue;
            }
            Word w = new Word();
            w.setWord(kv[0]);
            w.setCode(kv[1]);
            w.setOrder(searchSimplifiedOrder(kv[0]));
            if (single.contains(w.getWord())) {
                w.setLevel(2);
                full.add(w);
                fullCodes.add(w.getCode());
            } else if (w500.contains(w.getWord())) {
                group500.add(w);
            } else if (w1600.contains(w.getWord())) {
                group1600.add(w);
            }else if (w3800.contains(w.getWord())) {
                group3800.add(w);
            }else if (w5700.contains(w.getWord())) {
                group5700.add(w);
            } else {
                w.setLevel(90);
                groupOther.add(w);
            }
        }

        processGroups(group500, 0, 1, true);
        processGroups(group1600, 2, 1, true);
        processGroups(group3800, 4, 1, true);
        processGroups(group5700, 6, 1, true);
        Collections.sort(groupOther);
        Map<String, List<Word>> groupBySyllables = new HashMap<>();
        for (Word w : groupOther) {
            String code = w.getCode();
            if (!groupBySyllables.containsKey(code)) {
                groupBySyllables.put(code, new ArrayList<>());
            }
            groupBySyllables.get(code).add(w);
        }
        for (Map.Entry<String, List<Word>> entry : groupBySyllables.entrySet()) {
            String code = entry.getKey();
            List<Word> ws = entry.getValue();
            Collections.sort(ws);
            String code3 = code.substring(0,3);
            if (ws.size() == 1) {
                Word w = ws.get(0);
                if (!codeSet.containsKey(code3) && wGb2312.contains(w.getWord())) {
                    codeSet.put(code3, new AtomicInteger(1));
                    codeLenCounter[code.length()] += 1;
                    Word newWord = w.clone();
                    newWord.setCode(code3);
                    result.add(newWord);
                    full.add(w);
                    fullCodes.add(w.getCode());
                } else {
                    codeLenCounter[code.length()] += 1;
                    w.setCode(code);
                    if (w.getLevel() == 90) {
                        if (fullCodes.contains(code)) {
                            w.setLevel(92);
                        }
                        sp.add(w);
                    } else {
                        if (fullCodes.contains(code)) {
                            w.setLevel(w.getLevel() + 2);
                        }
                        result.add(w);
                    }
                    fullCodes.add(w.getCode());
                }
            } else {
                int start = 0;
                Word w = ws.get(0);
                if (!codeSet.containsKey(code3) && wGb2312.contains(w.getWord())) {
                    codeSet.put(code3, new AtomicInteger(1));
                    codeLenCounter[code.length()] += 1;
                    Word newWord = w.clone();
                    newWord.setCode(code3);
                    result.add(newWord);
                    full.add(w);
                    fullCodes.add(w.getCode());
                    start = 1;
                }

                for (int i = start; i < ws.size(); i++) {
                    w = ws.get(i);
                    if (!codeSet.containsKey(code) && !fullCodes.contains(code)) {
                        codeSet.put(code, new AtomicInteger(1));
                        codeLenCounter[code.length()] += 1;
                        if (w.getLevel() == 90) {
                            sp.add(w);
                        } else {
                            result.add(w);
                        }
                        fullCodes.add(w.getCode());
                    } else {
                        if (w.getLevel() == 90) {
                            codeLenCounter[code.length()] += 1;
                            if (wGb2312.contains(w.getWord())) {
                                w.setLevel(100);
                            } else {
                                w.setCode(w.getCode() + "z");
                                w.setLevel(110);
                            }
                            sp.add(w);
                        } else {
                            codeLenCounter[code.length()] += 1;
                            w.setLevel(95);
                            result.add(w);
                        }
                    }

                }
            }
        }

        Iterator<Word> iter = full.iterator();
        while (iter.hasNext()) {
            Word w = iter.next();
            String code = w.getCode();
            if (!codeSet.containsKey(code)) {
                codeLenCounter[code.length()] += 1;
                w.setLevel(200);
                //                sp.add(w);
                //                iter.remove();
            } else {
                codeLenCounter[code.length()] += 1;
                w.setLevel(300);
                //                sp.add(w);
                //                iter.remove();
            }
        }
        for (int i = 0; i < codeLenCounter.length; i++) {
            System.out.printf("%s, ", codeLenCounter[i]);
        }
        System.out.println();
        Map<String, List<Word>> results = new HashMap<>();
        results.put("result", result);
        results.put("full", full);
        results.put("sp", sp);
        return results;
    }

    private static void processGroups(List<Word> group, int offset, int dup, boolean allowCode3) {

        for (Word w : group) {
            try {
                String code2 = w.getCode().substring(0,2);
                String code3 = w.getCode().substring(0,3);
                String code = w.getCode();
                if (!codeSet.containsKey(code2)) {
                    codeSet.put(code2, new AtomicInteger(1));
                    codeLenCounter[code2.length()] += 1;
                    w.setLevel(10 + offset);
                    Word newWord = w.clone();
                    newWord.setCode(code2);
                    result.add(newWord);
                    full.add(w);
                    fullCodes.add(w.getCode());
                } else if (codeSet.containsKey(code2) && codeSet.get(code2).get() < dup) {
                    codeSet.get(code2).incrementAndGet();
                    String newCode  = code2 + padding[codeSet.get(code2).get()-1];
                    codeLenCounter[newCode.length()] += 1;
                    w.setLevel(10 + offset);
                    Word newWord = w.clone();
                    newWord.setCode(newCode);
                    result.add(newWord);
                    full.add(w);
                    fullCodes.add(w.getCode());
                } else if (!codeSet.containsKey(code3)) {
                    codeSet.put(code3, new AtomicInteger(1));
                    codeLenCounter[code3.length()] += 1;
                    w.setLevel(20+ offset);
                    Word newWord = w.clone();
                    newWord.setCode(code3);
                    result.add(newWord);
                    full.add(w);
                    fullCodes.add(w.getCode());
                } else if (allowCode3 && codeSet.containsKey(code3) && codeSet.get(code3).get() < dup) {
                    codeSet.get(code3).incrementAndGet();
                    String newCode  = code3 + padding[codeSet.get(code2).get()-1];
                    codeLenCounter[newCode.length()] += 1;
                    w.setLevel(20+ offset);
                    Word newWord = w.clone();
                    newWord.setCode(newCode);
                    result.add(newWord);
                    full.add(w);
                    fullCodes.add(w.getCode());
                } else {
                    if (group500.contains(w) ) {
                        System.out.printf("$ddcmd(\"%s\", \"%s[]\")\t%s%n", w.getWord(), w.getWord(),  w.getCode());
                    }
                    w.setLevel(50+ offset);
                    groupOther.add(w);
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                System.out.println("w = " + w);
            }
        }
    }



}
