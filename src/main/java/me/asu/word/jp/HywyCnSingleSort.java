package me.asu.word.jp;

import lombok.extern.slf4j.Slf4j;
import me.asu.word.Word;
import me.asu.word.shortern.GlobalVariables;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static me.asu.cli.command.cnsort.Orders.searchSimplifiedOrder;

@Slf4j
public class HywyCnSingleSort {
    char[] padding = "abcdefghijklmnopqrstuvwxy".toCharArray();
    char x = 'z';
    GlobalVariables gv = new GlobalVariables();
    Set<String> oneSetColl = new HashSet<>();
    Set<String> veryShort = new HashSet<>();
    Set<String> hz = new HashSet<>();

    public Map<String, List<Word>> makeShort(List<Word> lines,
                                             List<String> oneSet) {
        processOneSet(oneSet);
        if (oneSet != null) { oneSetColl.addAll(oneSet); }

        group(lines);

        processGroups();
        postProcess();

        return makeResult();
    }

    private void reOrder(List<Word> lines) {
        for (Word word : lines) {
            String w = word.getWord();
            Integer freq = gv.getJpOrder(w);
            word.setOrder(freq);
        }
        lines.sort((a, b)->{
           int delta = a.getOrder()-b.getOrder();
           if (delta != 0) return delta;
           delta = a.getLevel() - b.getLevel();
           if (delta !=0 ) return delta;
           return a.getCode().compareTo(b.getCode());
        });
    }

    protected void processOneSet(List<String> oneSet) {
        if (oneSet == null || oneSet.isEmpty()) { return; }
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

    /**
     * 按常用程度編碼
     * @param lines
     */
    private void group(List<Word> lines) {
        if (lines == null) {
            return;
        }
        log.info("Group {} lines into groups.", lines.size());
//        lines.sort(Word::compare);
        for (int i = 0; i < lines.size(); i++) {
            Word w = lines.get(i);
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

            /*
              1 JpCommon
              2 common
              3 gb2312_1
              4 gb2312
              5 hk
              6 big5-common
              7 jp-l1, jp-l2
              8 big5,other gb
              9 jp-l3,jp-l4
             10 other
             */
             if (gv.isInJpCommon(hz)) {
                w.setLevel(10);
                gv.addToGroup1(w);
             } else if (gv.isInJpLevel1(hz)||gv.isInJpLevel2(hz)) {
                 w.setLevel(60);
                 gv.addToGroup7(w);
             } else if (gv.isInC2000(hz)) {
                 w.setLevel(20);
                 gv.addToGroup2(w);
             } else if (gv.isInBig5Hkscs(hz)) {
                 w.setLevel(70);
                 gv.addToGroup5(w);
             } else if (gv.isInBig5Common(hz)) {
                 w.setLevel(30);
                 gv.addToGroup6(w);
             } else if (gv.isInBig5(hz)) {
                 w.setLevel(80);
                 gv.addToGroup8(w);
             }else if (gv.isInGB2312_1(hz)) {
                w.setLevel(40);
                gv.addToGroup3(w);
            }  else if (gv.isInGB2312(hz)) {
                w.setLevel(50);
                gv.addToGroup4(w);
            } else if (gv.isInGb(hz)) {
                w.setLevel(80);
                gv.addToGroup8(w);
            } else if ( gv.isInJpLevel3(hz)||gv.isInJpLevel4(hz)) {
                w.setLevel(90);
                gv.addToGroup9(w);
            } else {
                w.setLevel(100);
                gv.addToGroupOther(w);
            }
            if (i % 1000 == 0) {
                log.info("Processed {} lines.", i);
            }
        }
        log.info("Processed {} lines.", lines.size());
        log.info("group1: {}", gv.getGroup1().size());
        log.info("group2: {}", gv.getGroup2().size());
        log.info("group3: {}", gv.getGroup3().size());
        log.info("group4: {}", gv.getGroup4().size());
        log.info("group5: {}", gv.getGroup5().size());
        log.info("group6: {}", gv.getGroup6().size());
        log.info("group7: {}", gv.getGroup7().size());
        log.info("group8: {}", gv.getGroup8().size());
        log.info("group9: {}", gv.getGroup9().size());
        log.info("groupOther: {}", gv.getGroupOther().size());
        Collections.sort(gv.getGroup4());
        Collections.sort(gv.getGroup5());
        Collections.sort(gv.getGroupOther());
    }

    private void processGroups() {
        /*
              1 JpCommon
              2 common
              3 gb2312_1
              4 gb2312
              5 hk
              6 big5-common
              7 jp-l1, jp-l2
              8 big5,other gb
              9 jp-l3,jp-l4
             10 other
             */
        log.info("Processing groups ...");
        processGroupCommon(gv.getGroup1());
        processGroupCommon(gv.getGroup2());
        processGroupLevel2(gv.getGroup3());
        processGroupLevel2(gv.getGroup4());
        processGroupLevel2(gv.getGroup5());
        processGroupLevel2(gv.getGroup6());
        processGroupLevel2(gv.getGroup7());
        processGroupLevel2(gv.getGroup8());
        processGroupLevel2(gv.getGroup9());
        processGroupOther( gv.getRemain(),gv.getGroupOther());
    }
    private void processGroupCommon(List<Word>... wordList) {
        List<Word> list = joinList(wordList);
        for (Word w : list) {
            if (hz.contains(w.getWord())) {
                gv.addToRemain(w);
                continue;
            } else {
                hz.add(w.getWord());
            }

            Word clone = w.clone();
            String code = w.getCode();

            boolean accept = false;
            if (!veryShort.contains(w.getWord())) {
                String code1 = code.substring(0, 1);
                String code2 = code.substring(0, 2);
                String code3 = code.substring(0, 3);

                String[] codes = {code1, code2
//                        ,code3
                };
                for (int i = 0; i < codes.length; i++) {
                    String c = codes[i];
                    if (gv.isNotInCodeSet(c)) {
                        w.setCode(c);
                        w.setCodeExt("");
                        addToResult(w);
                        gv.increaseCodeLengthCounter(c.length())
                                .updateCodeSetCounter(c);
                        accept = true;
                        if (c.length() <= 3) veryShort.add(w.getWord());
                        break;
                    }
                }
            }
            if (!accept && gv.isNotInCodeSet(code)) {
                w.setCode(code);
                w.setCodeExt("");
                addToResult(w);
                gv.increaseCodeLengthCounter(code.length())
                        .updateCodeSetCounter(code);
                accept = true;

            }

            if (accept) {
                clone.setCodeExt("");
                gv.addToFull(clone);
            } else {
                for (char c : padding) {
                    String newCode = code + c;
                    if (gv.isNotInCodeSet(newCode)) {
                        w.setCode(newCode);
                        addToResult(w);
                        w.setCodeExt("");
                        gv.increaseCodeLengthCounter(newCode.length())
                                .updateCodeSetCounter(newCode);
                        accept = true;
                        break;
                    }
                }

                if (!accept) {
                    String newCode = code + x;
                    w.setCode(newCode);
                    addToResult(w);
                    w.setCodeExt("");
                    gv.increaseCodeLengthCounter(newCode.length())
                            .updateCodeSetCounter(newCode);
                    accept = true;
                }

            }
        }
    }
    private void processGroupLevel2(List<Word>... wordList) {
        List<Word> list = joinList(wordList);
        reOrder(list);
        for (Word w : list) {
            if (hz.contains(w.getWord())) {
                gv.addToRemain(w);
                continue;
            } else {
                hz.add(w.getWord());
            }
            Word clone = w.clone();

            String code = w.getCode();

            boolean accept = false;
//            if (!veryShort.contains(w.getWord())) {
//                String code3 = code.substring(0, 3);
//
//                String[] codes = {code3};
//                for (int i = 0; i < codes.length; i++) {
//                    String c = codes[i];
//                    if (gv.isNotInCodeSet(c)) {
//                        w.setCode(c);
//                        w.setCodeExt("");
//                        addToResult(w);
//                        gv.increaseCodeLengthCounter(c.length())
//                                .updateCodeSetCounter(c);
//                        accept = true;
//                        if (c.length() <= 3) veryShort.add(w.getWord());
//                        break;
//                    }
//                }
//            }
            if (!accept && gv.isNotInCodeSet(code)) {
                w.setCode(code);
                w.setCodeExt("");
                addToResult(w);
                gv.increaseCodeLengthCounter(code.length())
                        .updateCodeSetCounter(code);
                accept = true;
            }

            if (accept) {
                clone.setCodeExt("");
                gv.addToFull(clone);
            } else {
                for (char c : padding) {
                    String newCode = code + c;
                    if (gv.isNotInCodeSet(newCode)) {
                        w.setCode(newCode);
                        addToResult(w);
                        w.setCodeExt("");
                        gv.increaseCodeLengthCounter(newCode.length())
                                .updateCodeSetCounter(newCode);
                        accept = true;
                        break;
                    }
                }

                if (!accept) {
                    String newCode = code + x;
                    w.setCode(newCode);
                    addToResult(w);
                    w.setCodeExt("");
                    gv.increaseCodeLengthCounter(newCode.length())
                            .updateCodeSetCounter(newCode);
                    accept = true;
                }
            }
        }
    }

    private void processGroupOther(List<Word>... wordList) {
        List<Word> list = joinList(wordList);
        reOrder(list);
        for (Word w : list) {
            Word clone = w.clone();

            String code = w.getCode();

            boolean accept = false;

            if (gv.isNotInCodeSet(code)) {
                w.setCode(code);
                w.setCodeExt("");
                addToResult(w);
                gv.increaseCodeLengthCounter(code.length())
                        .updateCodeSetCounter(code);
                accept = true;
            }

            if (accept) {
                clone.setCodeExt("");
                gv.addToFull(clone);
            } else {
                for (char c : padding) {
                    String newCode = code + c;
                    if (gv.isNotInCodeSet(newCode)) {
                        w.setCode(newCode);
                        addToResult(w);
                        w.setCodeExt("");
                        gv.increaseCodeLengthCounter(newCode.length())
                                .updateCodeSetCounter(newCode);
                        accept = true;
                        break;
                    }
                }

                if (!accept) {
                    String newCode = code + x;
                    w.setCode(newCode);
                    addToResult(w);
                    w.setCodeExt("");
                    gv.increaseCodeLengthCounter(newCode.length())
                            .updateCodeSetCounter(newCode);
                    accept = true;
                }
            }
        }
    }


    private void addToResult(Word w) {
        String hz = w.getWord();
        if (gv.isInC2000(hz) ||gv.isInJpCommon(hz)) {
            gv.addToResult(w);
        } else if (gv.isInGB2312_1(hz)||gv.isInBig5Hkscs(hz)) {
            gv.addToResult2(w);
        } else if (gv.isInGb(hz)||gv.isInBig5Common(hz)){
            gv.addToResult3(w);
        } else if (gv.isInBig5(hz) || gv.isInJpLevel1(hz) || gv.isInJpLevel2(hz)) {
            gv.addToResult4(w);
        }  else {
	        gv.addToResult5(w);
        }
    }


    private List<Word> joinList(List<Word>... wordList) {
        int size = 0;
        for (List<Word> wList : wordList) {
            size += wList.size();
        }
        List<Word> list = new ArrayList<>(size);
        for (List<Word> wList : wordList) {
            for (int i = 0, wListSize = wList.size(); i < wListSize; i++) {
                Word word = wList.get(i);
//                if ("漢".equals(word.getWord())) { System.out.println(word + ": " +i); }
//                if ("汗".equals(word.getWord())) { System.out.println(word + ": " +i); }
                list.add(word);
            }

        }
//        Collections.sort(list, Word::compare);
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
		List<Word> words = joinList(result, result2
                 , result3 ,result4 , result5
                 , result6
                 , result7
		);
		Map<String, AtomicInteger> stat = new HashMap<>();
		for (Word w : words) {
			String c = w.getCode();
			stat.computeIfAbsent(c, k -> new AtomicInteger(0));
			stat.get(c).incrementAndGet();
		}
		Map<Integer, List<String>> dup = new TreeMap<>();
		stat.forEach((c,n)->{
			int cnt = n.get();
			dup.computeIfAbsent(cnt, k->new LinkedList<>());
			dup.get(cnt).add(c);
		});

		dup.forEach((c,l)->{
//		    if (c > 5) System.out.println(l);
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
        List<Word>  list = joinList(result,result2,result3,result4,result5);
        Set<String> codes = new HashSet<>();
        for (Word word : list) {
            codes.add(word.getCode());
        }
        Iterator<Word> iter = gv.getFull().iterator();
        while (iter.hasNext()) {
            Word w = iter.next();
            String code = w.getCode();
            String hz = w.getWord();
            if (!codes.contains(code)) {
                w.setLevel(200);
                gv.addToResult6(w);
//                gv.increaseCodeLengthCounter(code.length())
//                  .updateCodeSetCounter(code);
                codes.add(code);

            } else {
                w.setLevel(300);
                gv.addToResult7(w);
            }
            iter.remove();
        }
    }


}
