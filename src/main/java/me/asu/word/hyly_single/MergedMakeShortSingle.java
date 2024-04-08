package me.asu.word.hyly_single;

import static me.asu.cli.command.cnsort.Orders.searchSimplifiedOrder;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import lombok.extern.slf4j.Slf4j;
import me.asu.word.Word;
import me.asu.word.shortern.GlobalVariables;

/**
 * 用于顶功模式
 */
@Slf4j
public class MergedMakeShortSingle {

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
                String code1 = w.getCode().substring(0, 1);
                String code2 = w.getCode().substring(0, 1) + ";";
                String code3 = w.getCode().substring(0, 1) + "/";
                String code4 = w.getCode().substring(0, 2);
                boolean found = false;
                String[] col = {code1, code2, code3, code4};
                for (String s : col) {
                    if (oneSetColl.contains(hz + "\t" + s)) {
                        found = true;
                        break;
                    }
                }
//                boolean found = true;
                if (found) {
                    Word clone = w.clone();
                    String full = w.getCode() + w.getCodeExt();
                    clone.setCode(full);
                    clone.setCodeExt("");
                    continue;
                }
            }

            if (gv.isIn500Set(hz)) {
                w.setLevel(10);
                gv.getGroup1().add(w);
            } else if (gv.isIn1000Set(hz)) {
                w.setLevel(20);
                gv.addToGroup2(w);
            } else if (gv.isIn2000Set(hz)) {
                w.setLevel(30);
                gv.addToGroup3(w);
            } else if (gv.isIn4000Set(hz)) {
                w.setLevel(40);
                gv.addToGroup4(w);
            } else if (gv.isInGb(hz)) {
                w.setLevel(50);
                gv.addToGroup5(w);
            } else if (gv.isInJpCommon(hz)) {
                w.setLevel(60);
                gv.addToGroup6(w);
            } else if (gv.isInBig5Common(hz)) {
                w.setLevel(70);
                gv.addToGroup7(w);
            } else if (gv.isInBig5Hkscs(hz)) {
                w.setLevel(80);
                gv.addToGroup8(w);
            } else {
                w.setLevel(90);
                gv.addToGroupOther(w);
            }
            if (i % 1000 == 0) {
                log.info("Processed {} lines.", i);
            }
        }
        log.info("Processed {} lines.", lines.size());
        log.info("group 1: {}", gv.getGroup1().size());
        log.info("group 2: {}", gv.getGroup2().size());
        log.info("group 3: {}", gv.getGroup3().size());
        log.info("group 4: {}", gv.getGroup4().size());
        log.info("group 5: {}", gv.getGroup5().size());
        log.info("group 6: {}", gv.getGroup6().size());
        log.info("group 7: {}", gv.getGroup7().size());
        log.info("group 8: {}", gv.getGroup8().size());
        log.info("groupOther: {}", gv.getGroupOther().size());
    }

    private void processGroups() {
        log.info("Processing groups ...");
        processGroupLevel1(gv.getGroup1()); // 500
        processGroupLevel1(gv.getGroup2()); // 1000
        processGroupLevel1(gv.getGroup3()); // 2000
        processGroupLevel1(gv.getGroup4()); // 4000
        List<Word> remain = new ArrayList<>(gv.getRemain());
        gv.getRemain().clear();
        processGroupLevel2(remain);

        processGroupLevel2(gv.getGroup5()); // 8000
        processGroupLevel2(gv.getGroup6()); // common jp
        processGroupLevel2(gv.getGroup7()); // common big5
        processGroupLevel2(gv.getGroup8()); // hkscs
        processGroupLevel2(remain, gv.getGroupOther()); // other
    }

    private void processGroupLevel1(List<Word>... wordList) {
        List<Word> list = joinList(wordList);
        for (Word w : list) {
            Word clone = w.clone();
            String code = w.getCode();
            String hz = w.getWord();
            String code1 = code.substring(0, 1);
            String code2 = code.substring(0, 2);
            String thirdCode = code.substring(2, 3);
            if (gv.hzExist(hz)) {
                gv.addToRemain(w);
                continue;
            }

            if (gv.isNotInCodeSet(code1)) {
                w.setCode(code1);
                w.setCodeExt("");
                addToResult(w);
                gv.increaseCodeLengthCounter(code1.length())
                        .updateCodeSetCounter(code1);
                gv.hz(w.getWord());
                continue;
            }
            String newCode;
            switch (thirdCode) {
                case "1":
                    newCode = code2;
                    if (gv.isNotInCodeSet(newCode)) {
                        w.setCode(newCode);
                        w.setCodeExt("");
                        addToResult(w);
                        gv.increaseCodeLengthCounter(newCode.length())
                                .updateCodeSetCounter(newCode);
                        gv.hz(w.getWord());
                    } else {
                        gv.addToRemain(w);
                    }
                    break;
                case "2":
                    newCode = code2 + "6";
                    if (gv.isNotInCodeSet(newCode)) {
                        w.setCode(newCode);
                        w.setCodeExt("");
                        addToResult(w);
                        gv.increaseCodeLengthCounter(newCode.length())
                                .updateCodeSetCounter(newCode);
                        gv.hz(w.getWord());
                    } else {
                        gv.addToRemain(w);
                    }
                    break;
                case "3":
                    newCode = code2 + "3";
                    if (gv.isNotInCodeSet(newCode)) {
                        w.setCode(newCode);
                        w.setCodeExt("");
                        addToResult(w);
                        gv.increaseCodeLengthCounter(newCode.length())
                                .updateCodeSetCounter(newCode);
                        gv.hz(w.getWord());
                    } else {
                        gv.addToRemain(w);
                    }
                    break;
                case "4":
                    newCode = code2 + "4";
                    if (gv.isNotInCodeSet(newCode)) {
                        w.setCode(newCode);
                        w.setCodeExt("");
                        addToResult(w);
                        gv.increaseCodeLengthCounter(newCode.length())
                                .updateCodeSetCounter(newCode);
                        gv.hz(w.getWord());
                    } else {
                        gv.addToRemain(w);
                    }
                    break;
                case "5":
                    newCode = code2 + "7";
                    if (gv.isNotInCodeSet(newCode)) {
                        w.setCode(newCode);
                        w.setCodeExt("");
                        addToResult(w);
                        gv.increaseCodeLengthCounter(newCode.length())
                                .updateCodeSetCounter(newCode);
                        gv.hz(w.getWord());
                    } else {
                        gv.addToRemain(w);
                    }
                    break;
                default:
                    gv.addToRemain(w);
                    break;
            }

        }

    }


    private void processGroupLevel2(List<Word>... wordList) {
        List<Word> list = joinList(wordList);
        for (Word w : list) {
            Word clone = w.clone();
            String code = w.getCode();
            String hz = w.getWord();
            String code1 = code.substring(0, 1);
            String code2 = code.substring(0, 2);
            String thirdCode = code.substring(2, 3);
            String xmCode = code.substring(code.length() - 2, code.length() - 1);
            String xmCode2 = code.substring(code.length() - 2);

            if (gv.hzExist(hz)) {
                gv.addToRemain(w);
                continue;
            }

            if (gv.isNotInCodeSet(code1)) {
                w.setCode(code1);
                w.setCodeExt("");
                addToResult(w);
                gv.increaseCodeLengthCounter(code1.length())
                        .updateCodeSetCounter(code1);
                gv.hz(w.getWord());
                continue;
            }
            String newCode;
            switch (thirdCode) {
                case "1":
                    newCode = code2 + xmCode;
                    if (gv.isNotInCodeSet(newCode)) {
                        w.setCode(newCode);
                        w.setCodeExt("");
                        addToResult(w);
                        gv.increaseCodeLengthCounter(newCode.length())
                                .updateCodeSetCounter(newCode);
                        gv.hz(w.getWord());
                    } else {
                        newCode = code2 + xmCode2;
                        w.setCode(newCode);
                        w.setCodeExt("");
                        addToResult(w);
                        gv.increaseCodeLengthCounter(newCode.length())
                                .updateCodeSetCounter(newCode);
                        gv.hz(w.getWord());
                    }
                    break;
                case "2":
                    newCode = code2 + "6" + xmCode;
                    if (gv.isNotInCodeSet(newCode)) {
                        w.setCode(newCode);
                        w.setCodeExt("");
                        addToResult(w);
                        gv.increaseCodeLengthCounter(newCode.length())
                                .updateCodeSetCounter(newCode);
                        gv.hz(w.getWord());
                    } else {
                        newCode = code2 + "6" + xmCode2;
                        w.setCode(newCode);
                        w.setCodeExt("");
                        addToResult(w);
                        gv.increaseCodeLengthCounter(newCode.length())
                                .updateCodeSetCounter(newCode);
                        gv.hz(w.getWord());
                    }
                    break;
                case "3":
                    newCode = code2 + "3" + xmCode;
                    if (gv.isNotInCodeSet(newCode)) {
                        w.setCode(newCode);
                        w.setCodeExt("");
                        addToResult(w);
                        gv.increaseCodeLengthCounter(newCode.length())
                                .updateCodeSetCounter(newCode);
                        gv.hz(w.getWord());
                    } else {
                        newCode = code2 + "3" + xmCode2;
                        w.setCode(newCode);
                        w.setCodeExt("");
                        addToResult(w);
                        gv.increaseCodeLengthCounter(newCode.length())
                                .updateCodeSetCounter(newCode);
                        gv.hz(w.getWord());
                    }
                    break;
                case "4":
                    newCode = code2 + "4" + xmCode;
                    if (gv.isNotInCodeSet(newCode)) {
                        w.setCode(newCode);
                        w.setCodeExt("");
                        addToResult(w);
                        gv.increaseCodeLengthCounter(newCode.length())
                                .updateCodeSetCounter(newCode);
                        gv.hz(w.getWord());
                    } else {
                        newCode = code2 + "4" + xmCode2;
                        w.setCode(newCode);
                        w.setCodeExt("");
                        addToResult(w);
                        gv.increaseCodeLengthCounter(newCode.length())
                                .updateCodeSetCounter(newCode);
                        gv.hz(w.getWord());
                    }
                    break;
                case "5":
                    newCode = code2 + "7" + xmCode;
                    if (gv.isNotInCodeSet(newCode)) {
                        w.setCode(newCode);
                        w.setCodeExt("");
                        addToResult(w);
                        gv.increaseCodeLengthCounter(newCode.length())
                                .updateCodeSetCounter(newCode);
                        gv.hz(w.getWord());
                    } else {
                        newCode = code2 + "7" + xmCode2;
                        w.setCode(newCode);
                        w.setCodeExt("");
                        addToResult(w);
                        gv.increaseCodeLengthCounter(newCode.length())
                                .updateCodeSetCounter(newCode);
                        gv.hz(w.getWord());
                    }
                    break;
            }
        }

    }

    //    private void padding3(Word w) {
//        String code = w.getCode();
//        String finalCode = code + "v" + "z";
//        w.setCode(finalCode);
//        addToResult(w);
//        gv.increaseCodeLengthCounter(finalCode.length())
//                .updateCodeSetCounter(finalCode);
//    }
//
//    private boolean padding2(Word w) {
//        String code = w.getCode();
//        for (char c = 'a'; c <= 'z'; c++) {
//            String newCode = code + 'v' + c;
//            if (gv.isNotInCodeSet(newCode)) {
//                w.setCode(newCode);
//                addToResult(w);
//                gv.increaseCodeLengthCounter(newCode.length())
//                        .updateCodeSetCounter(newCode);
//                return true;
//            }
//        }
//        return false;
//    }
//
//    private void padding(Word w) {
//        String[] padding = {"a", "e", "d", "f", "g", "h", "i", "j", "k", "l"};
//        String code = w.getCode();
//        boolean accept = false;
//        for (String s : padding) {
//            String newCode = code + s;
//            if (gv.isNotInCodeSet(newCode)) {
//                accept = true;
//                w.setCode(newCode);
//                addToResult(w);
//                gv.increaseCodeLengthCounter(newCode.length())
//                        .updateCodeSetCounter(newCode);
//                break;
//            }
//        }
//        if (!accept) {
//            String newCode = code + 's';
//            w.setCode(newCode);
//            addToResult(w);
//            gv.increaseCodeLengthCounter(newCode.length())
//                    .updateCodeSetCounter(newCode);
//        }
//    }

    private void addToResult(Word w) {
        String hz = w.getWord();
        if (gv.isIn500Set(hz) || gv.isIn1000Set(hz) || gv.isIn2000Set(hz)) {
            gv.addToResult(w);
        } else if (gv.isIn4000Set(hz)) {
            gv.addToResult2(w);
        } else if (gv.isInGb(hz)) {
            gv.addToResult3(w);
        } else if (gv.isInJpCommon(hz)) {
            gv.addToResult4(w);
        } else if (gv.isInBig5(hz)) {
            gv.addToResult5(w);
        } else if (gv.isInBig5Hkscs(hz)) {
            gv.addToResult6(w);
        } else {
            gv.addToResult7(w);
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
        List<Word> result8 = gv.getResult8();
        List<Word> result9 = gv.getResult9();
        List<Word> words = joinList(result, result2, result3
                , result4, result5, result6, result7, result8, result9
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
        Iterator<Word> iter = gv.getFull().iterator();
        while (iter.hasNext()) {
            Word w = iter.next();
            String code = w.getCode();
            String hz = w.getWord();
            if (gv.isNotInCodeSet(code)) {
                w.setLevel(200);
                gv.addToResult7(w);
                gv.increaseCodeLengthCounter(code.length())
                        .updateCodeSetCounter(code);
                iter.remove();
            } else {
//                w.setLevel(300);
//                if (gv.isIn4200Set(hz)) {
//                    gv.addToResult2(w);
//                } else if (gv.isInGB2312Set(hz)) {
//                    gv.addToResult4(w);
//                } else {
//                    gv.addToResult6(w);
//                }
            }
        }
    }


}
