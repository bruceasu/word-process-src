package me.asu.word.hyly;

import lombok.extern.slf4j.Slf4j;
import me.asu.word.ResourcesFiles;
import me.asu.word.Word;
import me.asu.word.shortern.GlobalVariables;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static me.asu.cli.command.cnsort.Orders.searchSimplifiedOrder;

@Slf4j
public class MergedMakeShort {
    private static final int TOP_2000 = 2000;
    private static final String RESULT3_SUPPLEMENT = "result3Supplement";

    GlobalVariables gv = new GlobalVariables();
    Set<String> oneSetColl = new HashSet<>();
    Set<String> top1600 = ResourcesFiles.w1600();
    Set<String> commonYue = ResourcesFiles.commonYue();
    List<Word> result3Supplement = new ArrayList<>();

    public Map<String, List<Word>> makeSort(List<Word> lines,
                                            List<String> oneSet) {
        processOneSet(oneSet);
        if (oneSet != null) {
            oneSetColl.addAll(oneSet);
        }
        group(lines);

        processGroups();
        postProcess();

        return makeResult();
    }

    protected void processOneSet(List<String> oneSet) {
        if (oneSet == null || oneSet.isEmpty()) {
            return;
        }
        for (String str : oneSet) {
            String[] kv = str.split("\\s+");
            if (kv.length < 2) {
                continue;
            }
            Word w = new Word();
            w.setWord(kv[0]);
            w.setCode(kv[1]);
            w.setLevel(0);
            if (kv[0].length() == 1) {
                w.setOrder(searchSimplifiedOrder(kv[0]));
            } else {
                w.setOrder(0);
            }
            gv.addToResult(w);
            gv.updateCodeSetCounter(kv[1])
                    .increaseCodeLengthCounter(kv[1].length())
                    .addToSingle(kv[0]);
        }
    }

    private void group(List<Word> lines) {
        if (lines == null) {
            return;
        }
        log.info("Group {} lines into groups.", lines.size());
        lines.sort(Word::compare);
        for (int i = 0; i < lines.size(); i++) {
            Word w = lines.get(i);
            //gv.increaseCode3SetCounter(w.getCode() + w.getCodeExt().substring(0, 1));
            String hz = w.getWord();
            if (gv.isInSingleSet(hz)) {
                boolean found = true;
                if (found) {
                    Word clone = w.clone();
                    String full = w.getCode() + w.getCodeExt();
                    clone.setCode(full);
                    clone.setCodeExt("");
                    gv.addToFull(clone);
                    continue;
                }
            }

            if (w.getLevel() <= 1) {
                gv.addToGroup1(w); // 1级字
            } else if (w.getLevel() <= 2) {
                gv.addToGroup2(w); // 2级字
            } else if (w.getLevel() <= 3) {
                gv.addToGroup3(w); // 3 级字
            } else if (w.getLevel() <= 4) {
                gv.addToGroup4(w); // 4 级字
            } else if (w.getLevel() < 5) {
                gv.addToGroup5(w); // 非常用字L1
            } else if (w.getLevel() <= 6) {
                gv.addToGroup6(w); // 非常用字L2
            } else if (w.getLevel() <= 7) {
                gv.addToGroup7(w); // 非常用字L3
            } else if (w.getLevel() <= 8) {
                gv.addToGroup8(w); // 非常用字L4
            } else if (w.getLevel() <= 9) {
                gv.addToGroup9(w); // 其他字1
            } else {
                w.setLevel(90);
                gv.addToGroupOther(w); // 其他字2
            }

            if (i % 1000 == 0) {
                log.info("Processed {} lines.", i);
            }
        }
        log.info("Processed {} lines.", lines.size());
        log.info("getGroup1: {}", gv.getGroup1().size());
        log.info("getGroup2: {}", gv.getGroup2().size());
        log.info("getGroup3: {}", gv.getGroup3().size());
        log.info("getGroup4: {}", gv.getGroup4().size());
        log.info("getGroup5: {}", gv.getGroup5().size());
        log.info("getGroup6: {}", gv.getGroup6().size());
        log.info("getGroup7: {}", gv.getGroup7().size());
        log.info("getGroup8: {}", gv.getGroup8().size());
        log.info("getGroup9: {}", gv.getGroup9().size());
        log.info("groupOther: {}", gv.getGroupOther().size());
    }

    private void processGroups() {
        log.info("Processing groups ...");
        atMost3chars(gv.getGroup1());
        List<Word> remain = new ArrayList<>(gv.getRemain());
        gv.clearRemain();
        tryMost3chars(remain, gv.getGroup2());
        remain = new ArrayList<>(gv.getRemain());
        gv.clearRemain();
        luckWith3chars(remain, gv.getGroup3());
        remain = new ArrayList<>(gv.getRemain());
        gv.clearRemain();
        luckWith3chars(remain, gv.getGroup4());
        remain = new ArrayList<>(gv.getRemain());
        gv.clearRemain();
        fullChars(remain, gv.getGroup5(), gv.getGroup6(),
                gv.getGroup7(), gv.getGroup8(), gv.getGroup9(), gv.getGroupOther()); // 其他
    }

    // 1, 2, 3
    private void atMost3chars(List<Word>... wordList) {
        List<Word> list = joinList(wordList);
        Set<String> preferredCodes = collectPreferredCandidateCodes(list, true);
        for (Word w : list) {
            Word clone = w.clone();
            String code = w.getCode();
            List<String> codes = candidateShortCodes(w, code, true);
            boolean accept = false;
            for (String s : codes) {
                if (isPreferredCandidate(preferredCodes, s) && gv.isNotInCodeSet(s)) {
                    Word c = w.clone();
                    c.setCode(s);
                    c.setCodeExt("");
                    addToResult(c);
                    gv.increaseCodeLengthCounter(s.length()).updateCodeSetCounter(s);
                    accept = true;
                    break;
                }
            }
            if (accept) {
                clone.setCodeExt("");
                gv.addToFull(clone);
            } else {
                gv.addToRemain(w);
            }
        }
    }

    // 2, 3,
    private void tryMost3chars(List<Word>... wordList) {
        List<Word> list = joinList(wordList);
        Set<String> preferredCodes = collectPreferredCandidateCodes(list, true);

        for (Word w : list) {
            Word clone = w.clone();
            String code = w.getCode();
            List<String> codes = candidateShortCodes(w, code, true);
            boolean accept = false;
            for (String s : codes) {
                if (isPreferredCandidate(preferredCodes, s) && gv.isNotInCodeSet(s)) {
                    Word c = w.clone();
                    c.setCode(s);
                    c.setCodeExt("");
                    addToResult(c);
                    gv.increaseCodeLengthCounter(s.length())
                            .updateCodeSetCounter(s);
                    accept = true;
                    break;
                }
            }
            if (accept) {
                clone.setCodeExt("");
                gv.addToFull(clone);
            } else {
                gv.addToRemain(w);
            }
        }
    }

    // 3
    private void luckWith3chars(List<Word>... wordList) {
        List<Word> list = joinList(wordList);
        Set<String> preferredCodes = collectPreferredCandidateCodes(list, false);
        Map<String, List<Word>> map = new TreeMap<>();
        for (Word word : list) {
            final List<Word> words = map.computeIfAbsent(word.getCode(), k -> new LinkedList<>());
            words.add(word);
        }
        Iterator<Map.Entry<String, List<Word>>> iterator = map.entrySet().iterator();
        while(iterator.hasNext()) {
            final Map.Entry<String, List<Word>> next = iterator.next();
            final String key = next.getKey();
            final List<Word> value = next.getValue();
            if (value.size() == 2) {
                int start = 0;
                final Word word = value.get(0);
                final String code3 = shortCode3(word);
                if (code3 != null && canUseThreeShort(word, false)
                        && isPreferredCandidate(preferredCodes, code3)
                        && gv.isNotInCodeSet(code3)) {
                    final Word clone = word.clone();
                    word.setCode(code3);
                    addToResult(word);
                    gv.addToFull(clone);
                    gv.increaseCodeLengthCounter(code3.length())
                            .updateCodeSetCounter(code3);
                    start = 1;
                }
                for (int i = start; i < value.size(); i++) {
                    Word w =  value.get(i);
                    gv.addToRemain(w);
                }
                iterator.remove();
            }
        }
        iterator = map.entrySet().iterator();
        while(iterator.hasNext()) {
            final Map.Entry<String, List<Word>> next = iterator.next();
            final String key = next.getKey();
            final List<Word> value = next.getValue();
            if (value.size() >= 2) {
                int start = 0;
                final Word word = value.get(0);
                final String code3 = shortCode3(word);
                if (code3 != null && canUseThreeShort(word, false)
                        && isPreferredCandidate(preferredCodes, code3)
                        && gv.isNotInCodeSet(code3)) {
                    final Word clone = word.clone();
                    word.setCode(code3);
                    addToResult(word);
                    gv.addToFull(clone);
                    gv.increaseCodeLengthCounter(code3.length())
                            .updateCodeSetCounter(code3);
                    start = 1;
                }
                for (int i = start; i < value.size(); i++) {
                    Word w =  value.get(i);
                    gv.addToRemain(w);
                }
                iterator.remove();
            }
        }
        map.forEach((k, v)->{
            for (Word w : v) {
                Word clone = w.clone();
                String code = w.getCode();
                String code3 = shortCode3(w);
                if (code3 != null && canUseThreeShort(w, false)
                        && isPreferredCandidate(preferredCodes, code3)
                        && gv.isNotInCodeSet(code3)) {
                    w.setCode(code3);
                    addToResult(w);
                    gv.increaseCodeLengthCounter(code3.length())
                            .updateCodeSetCounter(code3);
                    gv.addToFull(clone);
                } else {
                    if (!gv.isNotInCodeSet(w.getCode())) {
                        w.setLevel(99);
                    }
                    addToResult(w);
                    gv.increaseCodeLengthCounter(code.length())
                            .updateCodeSetCounter(code);
                }
            }
        });

    }

    // 4
    private void fullChars(List<Word>... wordList) {
        List<Word> list = joinList(wordList);
        for (Word w : list) {
            String code = w.getCode();
            if (!gv.isNotInCodeSet(code)) {
                w.setLevel(99);
            }
            addToResult(w);
            gv.increaseCodeLengthCounter(code.length())
                    .updateCodeSetCounter(code);
        }
    }


    private void addToResult(Word w) {
        if (w.getLevel() <= 1) { // 1级字 前2000字
            gv.addToResult(w);
        } else if (w.getLevel() <= 2) { // 2级字 gb2312-1
            gv.addToResult2(w);
        } else if (w.getLevel() <= 3) { // 3 级字 gb2312
            gv.addToResult3(w);
        } else if (w.getLevel() <= 4) { // 4 级字 other gb
            gv.addToResult4(w);
        } else if (w.getLevel() <= 5) { // 非常用字L1
            gv.addToResult5(w);
        } else if (w.getLevel() <= 6) { // 非常用字L2
            gv.addToResult6(w);
        } else if (w.getLevel() <= 7) { // 非常用字L3
            gv.addToResult7(w);
        } else {
            gv.addToResult8(w);
        }
    }


    private List<Word> joinList(List<Word>... wordList) {
        int size = 0;
        for (List<Word> wList : wordList) {
            size += wList.size();
        }
        List<Word> list = new ArrayList<>(size);
        for (List<Word> wList : wordList) {
            list.addAll(wList);
        }
        Collections.sort(list, Word::compare);
        return list;
    }

    private void postProcess() {
        fullProcess();
        supplementThreeShorts();
        printCounter("Post process done!");
        statistic();
    }

    private void statistic() {
        // statistic
        List<Word> result = gv.getResult();
        List<Word> result2 = gv.getResult2();
        List<Word> result3 = gv.getResult3();
        List<Word> result4 = gv.getResult4();
        List<Word> result5 = gv.getResult5();
        List<Word> result6 = gv.getResult6();
        List<Word> result7 = gv.getResult7();
        List<Word> words = joinList(result, result2
                , result3
                , result4
//                , result5
//                 , result6
//                 , result7
        );
        Map<String, AtomicInteger> stat = new HashMap<>();
        for (Word w : words) {
            String c = w.getCode();
            stat.computeIfAbsent(c, k -> new AtomicInteger(0));
            stat.get(c).incrementAndGet();
        }
        Map<Integer, List<String>> dup = new TreeMap<>();
        stat.forEach((c, n) -> {
            int cnt = n.get();
            dup.computeIfAbsent(cnt, k -> new LinkedList<>());
            dup.get(cnt).add(c);
        });

        dup.forEach((c, l) -> {
            System.out.printf("dup %d has %d words.%n", c, l.size());
        });
    }

    private Map<String, List<Word>> makeResult() {
        Map<String, List<Word>> results = new HashMap<>();
        results.put("result", gv.getResult());
        results.put("result2", gv.getResult2());
        results.put("result3", gv.getResult3());
        results.put(RESULT3_SUPPLEMENT, result3Supplement);
        results.put("result4", gv.getResult4());
        results.put("result5", gv.getResult5());
        results.put("result6", gv.getResult6());
        results.put("result7", gv.getResult7());
        results.put("result8", gv.getResult8());
        results.put("result9", gv.getResult9());
        results.put("full", gv.getFull());
        return results;
    }

    private void printCounter(String x) {
        log.info(x);
        gv.printCounter();
    }

    private void fullProcess() {
        log.info("Processing full ...");
        List<Word> result = gv.getResult();
        List<Word> result2 = gv.getResult2();
        List<Word> result3 = gv.getResult3();
        List<Word> result4 = gv.getResult4();
        List<Word> result5 = gv.getResult5();
        List<Word> result6 = gv.getResult6();
        List<Word> result7 = gv.getResult7();
        List<Word> result8 = gv.getResult8();
        List<Word> list = joinList(result, result2, result3, result4,
                result5, result6, result7, result8);
        Set<String> codes = new HashSet<>();
        for (Word word : list) {
            codes.add(word.getCode());
        }
        Iterator<Word> iter = gv.getFull().iterator();
        while (iter.hasNext()) {
            Word w = iter.next();
            String code = w.getCode();
            if (!codes.contains(code)) {
                w.setLevel(200);
                gv.addToResult9(w);
                gv.increaseCodeLengthCounter(code.length())
                        .updateCodeSetCounter(code);
                codes.add(code);
                iter.remove();
            }
        }
    }

    private void fullProcess2() {
        log.info("Processing full ...");

        Iterator<Word> iter = gv.getFull().iterator();
        while (iter.hasNext()) {
            Word w = iter.next();
            String code = w.getCode();
            final String word = w.getWord();
            if (gv.isInLevel1(word) || gv.isInLevel2(word)) {
                w.setLevel(200);
                gv.addToResult7(w);
                gv.increaseCodeLengthCounter(code.length())
                        .updateCodeSetCounter(code);
                iter.remove();
            }
        }
    }

    private List<String> candidateShortCodes(Word word, String code, boolean includeTwoShort) {
        List<String> codes = new ArrayList<>(2);
        if (includeTwoShort && canUseTwoShort(word) && code.length() >= 2) {
            codes.add(code.substring(0, 2));
        }
        if (canUseThreeShort(word, true) && code.length() >= 3) {
            codes.add(code.substring(0, 3));
        }
        return codes;
    }

    private boolean canUseTwoShort(Word word) {
        return word != null && word.getWord() != null
                && word.getWord().length() == 1
                && top1600.contains(word.getWord());
    }

    private boolean canUseThreeShort(Word word, boolean top2000Only) {
        if (word == null || word.getWord() == null || word.getWord().length() != 1) {
            return false;
        }
        if (word.getOrder() > 0 && word.getOrder() <= TOP_2000) {
            return true;
        }
        return !top2000Only && isSupplementaryThreeShortWord(word.getWord());
    }

    private boolean isSupplementaryThreeShortWord(String hz) {
        return gv.isInGB2312(hz) || commonYue.contains(hz);
    }

    private String shortCode3(Word word) {
        if (word == null || word.getCode() == null || word.getCode().length() < 3) {
            return null;
        }
        return word.getCode().substring(0, 3);
    }

    private void supplementThreeShorts() {
        Set<String> exists = new HashSet<>();
        Set<String> usedCodes = new HashSet<>();
        Set<String> wordsWithShortCode = new HashSet<>();
        List<Word> allResults = joinList(gv.getResult(), gv.getResult2(), gv.getResult3(), gv.getResult4(),
                gv.getResult5(), gv.getResult6(), gv.getResult7(), gv.getResult8(), gv.getResult9());
        allResults.forEach(word -> {
            exists.add(word.getWord() + "\t" + word.getCode());
            if (word.getCode() != null) {
                usedCodes.add(word.getCode());
            }
            if (word.getCode() != null && word.getCode().length() <= 3) {
                wordsWithShortCode.add(word.getWord());
            }
        });

        for (Word resultWord : allResults) {
            String hz = resultWord.getWord();
            if (wordsWithShortCode.contains(hz) || !isSupplementaryThreeShortWord(hz)) {
                continue;
            }
            String code3 = shortCode3(resultWord);
            if (code3 == null) {
                continue;
            }
            if (!usedCodes.add(code3)) {
                continue;
            }
            String key = hz + "\t" + code3;
            if (!exists.add(key)) {
                continue;
            }
            Word clone = resultWord.clone();
            clone.setCode(code3);
            clone.setCodeExt("");
            result3Supplement.add(clone);
        }
    }

    private Set<String> collectPreferredCandidateCodes(List<Word> list, boolean includeTwoShort) {
        Set<String> preferredCodes = new HashSet<>();
        Set<String> seenCodes = new HashSet<>();
        Set<String> assignedWords = new HashSet<>();
        for (Word word : list) {
            if (!assignedWords.add(word.getWord())) {
                continue;
            }
            List<String> codes = candidateShortCodes(word, word.getCode(), includeTwoShort);
            for (String code : codes) {
                if (seenCodes.add(code)) {
                    preferredCodes.add(code);
                    break;
                }
            }
        }
        return preferredCodes;
    }

    private boolean isPreferredCandidate(Set<String> preferredCodes, String code) {
        return preferredCodes.contains(code);
    }
}