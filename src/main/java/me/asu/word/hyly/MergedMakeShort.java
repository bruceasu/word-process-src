package me.asu.word.hyly;

import lombok.extern.slf4j.Slf4j;
import me.asu.word.Word;
import me.asu.word.shortern.GlobalVariables;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static me.asu.cli.command.cnsort.Orders.searchSimplifiedOrder;

@Slf4j
public class MergedMakeShort {

    GlobalVariables gv = new GlobalVariables();
    Set<String> oneSetColl = new HashSet<>();

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

            if (kv[0].length() == 1) {
                w.setLevel(10);
                w.setOrder(searchSimplifiedOrder(kv[0]));
            } else {
                w.setLevel(1);
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

            if (gv.isIn500Set(hz) || gv.isIn1000Set(hz) || gv.isIn2000Set(hz)) {
                w.setLevel(10);
                gv.addToGroup1(w);
            } else if (gv.isInGB2312_1(hz) || gv.isIn4000Set(hz)) {
                w.setLevel(20);
                gv.addToGroup2(w);
            } else if (gv.isInGb(hz)) {
                w.setLevel(30);
                gv.addToGroup3(w);
            } else if (gv.isInJpCommon(hz)) {
                w.setLevel(40);
                gv.addToGroup4(w);
            } else if (gv.isInBig5Common(hz)) {
                w.setLevel(50);
                gv.addToGroup5(w);
            } else if (gv.isInBig5Hkscs(hz)) {
                w.setLevel(60);
                gv.addToGroup6(w);
            } else {
                w.setLevel(90);
                gv.addToGroupOther(w);
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
        processGroupLevel1(gv.getGroup1());
        processGroupLevel2(gv.getGroup2());
        List<Word> remain = new ArrayList<>(gv.getRemain());
        gv.clearRemain();
        processGroupLevel3(remain, gv.getGroup3());

        processGroupLevel4(gv.getGroup4()); // 日
        processGroupLevel4(gv.getGroup5()); // 繁
        processGroupLevel4(gv.getGroup6()); // 粤
        processGroupLevel4(gv.getGroup7());
        processGroupLevel4(gv.getGroup8());
        processGroupLevel4(gv.getGroup9());
        processGroupLevel4(gv.getGroupOther());
    }

    private void processGroupLevel1(List<Word>... wordList) {
        List<Word> list = joinList(wordList);
        for (Word w : list) {
            Word clone = w.clone();
            String code = w.getCode();
            String code1 = code.substring(0, 1);
            String code2 = code.substring(0, 2);
            String code3 = code.substring(0, 3);
            String[] codes = {code1, code2, code3, };

            boolean accept = false;
            for (String s : codes) {
                if (gv.isNotInCodeSet(s)) {
                    w.setCode(s);
                    w.setCodeExt("");
                    addToResult(w);
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

    private void processGroupLevel2(List<Word>... wordList) {
        List<Word> list = joinList(wordList);

        for (Word w : list) {
            Word clone = w.clone();
            String code = w.getCode();
            String code2 = code.substring(0, 2);
            String code3 = code.substring(0, 3);
            String[] codes = {code2, code3};
            boolean accept = false;
            for (String s : codes) {
                if (gv.isNotInCodeSet(s)) {
                    w.setCode(s);
                    w.setCodeExt("");
                    addToResult(w);
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

    private void processGroupLevel3(List<Word>... wordList) {
        List<Word> list = joinList(wordList);
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
                final String code3 = key.substring(0, 3);
                if (gv.isNotInCodeSet(code3)) {
                    final Word word = value.get(0);
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
                final String code3 = key.substring(0, 3);
                if (gv.isNotInCodeSet(code3)) {
                    final Word word = value.get(0);
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
                String code3 = code.substring(0, 3);
                if (gv.isNotInCodeSet(code3)) {
                    w.setCode(code3);
                    addToResult(w);
                    gv.increaseCodeLengthCounter(code3.length())
                            .updateCodeSetCounter(code3);
                    gv.addToFull(clone);
                } else {
                    addToResult(w);
                }
            }
        });

    }

    private void processGroupLevel4(List<Word>... wordList) {
        List<Word> list = joinList(wordList);
        for (Word w : list) {
            String code = w.getCode();
            addToResult(w);
            gv.increaseCodeLengthCounter(code.length())
                    .updateCodeSetCounter(code);
        }
    }

//    private void processOtherGroup( List<Word>... wordList) {
//        List<Word> wl = joinList(wordList);
//        Map<String, List<Word>> map = new TreeMap<>();
//        Map<String, List<Word>> map1 = new TreeMap<>();
//        Map<String, List<Word>> map2 = new TreeMap<>();
//        Map<String, List<Word>> mapM = new TreeMap<>();
//        for (Word word : wl) {
//            final String code = word.getCode();
//            final List<Word> words = map.computeIfAbsent(code, key->new LinkedList<>());
//            words.add(word);
//        }
//        map.forEach((k,v) -> {
//            if (v.size() == 1) {
//                map1.put(k, v);
//            } else if (v.size() == 2) {
//                map2.put(k, v);
//            } else {
//                mapM.put(k, v);
//            }
//        });
//
//        map2.forEach((k, v) -> {
//            String code3 = k.substring(0,3);
//            if (gv.isNotInCodeSet(code3)) {
//                final Word w = v.get(0);
//                Word clone = w.clone();
//                w.setCode(code3);
//                addToResult(w);
//                gv.increaseCodeLengthCounter(code3.length())
//                        .updateCodeSetCounter(code3);
//                gv.addToFull(clone);
//            } else {
//                for (Word word : v) {
//                    addToResult(word);
//                    gv.increaseCodeLengthCounter(k.length())
//                            .updateCodeSetCounter(k);
//                }
//            }
//        });
//        mapM.forEach((k, v) -> {
//            String code3 = k.substring(0,3);
//            int start  = 0;
//            if (gv.isNotInCodeSet(code3)) {
//                final Word w = v.get(0);
//                Word clone = w.clone();
//                w.setCode(code3);
//                addToResult(w);
//                gv.increaseCodeLengthCounter(code3.length())
//                        .updateCodeSetCounter(code3);
//                gv.addToFull(clone);
//                start = 1;
//            }
//            for (int i = start; i < v.size(); i++) {
//                Word w = v.get(i);
//                addToResult(w);
//                gv.increaseCodeLengthCounter(k.length())
//                        .updateCodeSetCounter(k);
//
//            }
//        });
//        map1.forEach((k, v) -> {
//            String code3 = k.substring(0,3);
//            if (gv.isNotInCodeSet(code3)) {
//                final Word w = v.get(0);
//                Word clone = w.clone();
//                w.setCode(code3);
//                addToResult(w);
//                gv.increaseCodeLengthCounter(code3.length())
//                        .updateCodeSetCounter(code3);
//                gv.addToFull(clone);
//            } else {
//                addToResult(v.get(0));
//                gv.increaseCodeLengthCounter(k.length())
//                        .updateCodeSetCounter(k);
//            }
//        });
//    }

    private void addToResult(Word w) {
        String hz = w.getWord();
        if (gv.isIn500Set(hz) || gv.isIn1000Set(hz) || gv.isIn2000Set(hz)) {
            gv.addToResult(w);
        } else if (gv.isInGB2312_1(hz) || gv.isIn4000Set(hz)) {
            gv.addToResult2(w);
        } else if (gv.isInGb(hz)) {
            gv.addToResult3(w);
        } else if (gv.isInJpCommon(hz)) {
            gv.addToResult4(w);
        } else if (gv.isInBig5(hz) || gv.isInBig5Hkscs(hz)) {
            gv.addToResult5(w);
        } else {
            gv.addToResult6(w);
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
        List<Word> words = joinList(result, result2, result3
                , result4
                //, result5 , result6, result7
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


}
