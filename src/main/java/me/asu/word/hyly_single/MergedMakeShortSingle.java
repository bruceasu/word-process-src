package me.asu.word.hyly_single;

import lombok.extern.slf4j.Slf4j;
import me.asu.word.Word;
import me.asu.word.shortern.GlobalVariables;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static me.asu.cli.command.cnsort.Orders.searchSimplifiedOrder;

@Slf4j
public class MergedMakeShortSingle {

    GlobalVariables gv = new GlobalVariables();
    Set<String> oneSetColl = new HashSet<>();
    Set<String> dup = new HashSet<>();

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
            String s = w.getWord() + w.getCode();
            dup.add(s);
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
        atMost2chars(gv.getGroup1(), gv.getGroup2());
        List<Word> remain = new ArrayList<>(gv.getRemain());
        gv.clearRemain();
        atMost3chars(remain, gv.getGroup3(), gv.getGroup4(), gv.getGroup5());
        remain = new ArrayList<>(gv.getRemain());
        gv.clearRemain();
        fullChars(remain, gv.getGroup6(), gv.getGroup7(), gv.getGroup8(), gv.getGroup9(), gv.getGroupOther()); // 其他
    }


    private void atMost3chars(List<Word>... wordList) {
        List<Word> list = joinList(wordList);
        for (Word w : list) {
            Word clone = w.clone();
            String code3 = w.getCode().substring(0, 3)
                    .replace('1', 'e')
                    .replace('2', 'u')
                    .replace('3', 'i')
                    .replace('4', 'o')
                    .replace('5', 'a');

            if (gv.isNotInCodeSet(code3)) {
                Word c = w.clone();
                c.setCode(code3);
                c.setCodeExt("");
                String s = c.getWord() + c.getCode();
                if (dup.contains(s)) continue;
                dup.add(s);
                addToResult(c);
                gv.increaseCodeLengthCounter(code3.length()).updateCodeSetCounter(code3);
                clone.setCodeExt("");
                gv.addToFull(clone);
                continue;
            }

            gv.addToRemain(w);
        }
    }

    private void atMost2chars(List<Word>... wordList) {
        List<Word> list = joinList(wordList);
        for (Word w : list) {
            Word clone = w.clone();
            String code2 = w.getCode().substring(0, 2);

            if (gv.isNotInCodeSet(code2) || gv.getCodeSetCount(code2) < 1) {
                Word c = w.clone();
                c.setCode(code2);
                c.setCodeExt("");
                String s = c.getWord() + c.getCode();
                if (dup.contains(s)) continue;
                dup.add(s);
                addToResult(c);
                gv.increaseCodeLengthCounter(code2.length()).updateCodeSetCounter(code2);
                clone.setCodeExt("");
                gv.addToFull(clone);
                continue;
            }

            gv.addToRemain(w);
        }
    }

    private void fullChars(List<Word>... wordList) {
        List<Word> list = joinList(wordList);
        for (Word w : list) {
            String code = w.getCode().substring(0, 4)
                    .replace('1', 'e')
                    .replace('2', 'u')
                    .replace('3', 'i')
                    .replace('4', 'o')
                    .replace('5', 'a');
            if (!gv.isNotInCodeSet(code)) {
                w.setLevel(99);
            }
            String s = w.getWord() + w.getCode();
            if (dup.contains(s)) continue;
            dup.add(s);
            w.setCode(code);
            addToResult(w);
            gv.increaseCodeLengthCounter(code.length())
                    .updateCodeSetCounter(code);
        }
    }


    private void addToResult(Word w) {
        if (w.getLevel() <= 3) { // gb2312
            gv.addToResult(w);
        } else if (w.getLevel() == 4) { // jp hk
            gv.addToResult2(w);
        } else if (w.getLevel() == 5) { // other gb
            gv.addToResult3(w);
        } else if (w.getLevel() <= 8) { // other big5 common
            gv.addToResult4(w);
        } else {
            if (w.getLevel() == 99 && (gv.isInJpCommon(w.getWord()))) {
                gv.addToResult5(w); // other with dup
            } else {
                gv.addToResult6(w); // other with dup
            }

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
//        fullProcess();
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
        results.put("result4", gv.getResult4());
        results.put("result5", gv.getResult5());
        results.put("result6", gv.getResult6());
        results.put("result7", gv.getResult7());
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
        List<Word> list = joinList(result, result2, result3, result4, result5, result6);
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
                gv.addToResult7(w);
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
}