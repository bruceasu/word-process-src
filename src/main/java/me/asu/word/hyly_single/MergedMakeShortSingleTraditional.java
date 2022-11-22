package me.asu.word.hyly_single;

import static me.asu.cli.command.cnsort.Orders.searchSimplifiedOrder;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.extern.slf4j.Slf4j;
import me.asu.word.Word;
import me.asu.word.shortern.GlobalVariablesTraditional;

/**
 * 用于顶功模式
 */
@Slf4j
public class MergedMakeShortSingleTraditional {

    GlobalVariablesTraditional gv         = new GlobalVariablesTraditional();
    Set<String>     oneSetColl = new HashSet<>();

    public Map<String, List<Word>> makeSort(List<Word> lines,
                                            List<String> oneSet) {
        processOneSet(oneSet);
        if (oneSet != null) { oneSetColl.addAll(oneSet); }
        group(lines);

        processGroups();
        postProcess();

        return makeResult();
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

    private void group(List<Word> lines) {
        if (lines == null) {
            return;
        }
        log.info("Group {} lines into groups.", lines.size());
        lines.sort(Word::compare);
        for (int i = 0; i < lines.size(); i++) {
            Word w = lines.get(i);
            String hz = w.getWord();
            if (gv.isInSingleSet(hz)) {
                Word clone = w.clone();
                String full = w.getCode() + w.getCodeExt();
                clone.setCode(full);
                clone.setCodeExt("");
                gv.addToFull(clone);
                continue;
            }

            if (gv.isInBig5Common(hz)) {
                w.setLevel(10);
                gv.addToTraditionCommon(w);
            } else if (gv.isInBig5Hkscs(hz)) {
                w.setLevel(20);
                gv.addToTradition(w);
            } else if (gv.isInGeneralSpecification(hz)) {
                w.setLevel(30);
                gv.addToSimplification(w);
            } else if (gv.isInJapanese(hz)) {
                w.setLevel(40);
                gv.addToJapanese(w);
            } else {
                w.setLevel(90);
                gv.addToGroupOther(w);
            }
            if (i % 1000 == 0) {
                log.info("Processed {} lines.", i);
            }
        }
        log.info("Processed {} lines.", lines.size());
        log.info("groupTraditionCommon: {}", gv.getGroupTraditionCommon().size());
        log.info("groupTradition: {}", gv.getGroupTradition().size());
        log.info("groupJapanese: {}", gv.getGroupJapanese().size());
        log.info("groupOther: {}", gv.groupOther.size());
    }


    private void processGroups() {
        log.info("Processing groups ...");
        processGroupLevel1(gv.getGroupTraditionCommon(), gv.getGroupTradition());
        List<Word> remain = new ArrayList<>(gv.getRemain());
        remain.clear();
        processGroupLevel2(remain, gv.getGroupSimplification(), gv.getGroupJapanese(), gv.getGroupOther());
    }

    private void processGroupLevel1(List<Word>... wordList) {
        List<Word> list = joinList(wordList);
        processOneAndTwo(list);
        List<Word> remain = new ArrayList<>(gv.getRemain());
        gv.getRemain().clear();

        Map<String, List<Word>> group = new TreeMap<>();
        for (Word w : remain) {
            String code = w.getCode();
            group.putIfAbsent(code, new ArrayList<>());
            group.get(code).add(w);
        }
        group.forEach((k,v)->{
            for (int i = 0, vSize = v.size(); i < vSize; i++) {
                Word w = v.get(i);
                String hz = w.getWord();
                if (gv.isInBig5Common(hz) && gv.getCodeSetCount(k) <3) {
                    addToResult(w);
                    gv.increaseCodeLengthCounter(k.length())
                      .updateCodeSetCounter(k);
                } else if (gv.isInBig5Hkscs(hz) && gv.getCodeSetCount(k) <2) {
                    addToResult(w);
                    gv.increaseCodeLengthCounter(k.length())
                      .updateCodeSetCounter(k);
                } else {
                    gv.addToRemain(w);
                }
            }
        });
    }

    private void processOneAndTwo(List<Word> list) {

        for (Word w : list) {
            Word clone = w.clone();
            String code = w.getCode();
            String code1 = code.substring(0, 1);
            String code2 = code.substring(0, 2);
            String[] codes = {code1, code2};
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
        processOneAndTwo(list);
        List<Word> remain = gv.getRemain();

        Map<String, Set<Word>> m = new TreeMap<>();
        for (Word w : remain) {
            String code = w.getCode();
            w.setCodeExt("");
            m.computeIfAbsent(code, k -> new HashSet<>());
            m.get(code).add(w);
        }
        remain.clear();

        m.forEach((k, l) -> {
            List<Word> wl = new ArrayList<>(l.size());
            wl.addAll(l);
            wl.sort(Word::compareTo);
            for (Word w : wl) {
                if (gv.isNotInCodeSet(k)) {
                    addToResult(w);
                    gv.increaseCodeLengthCounter(k.length())
                      .updateCodeSetCounter(k);
                } else {
                    boolean accept = padding(w);
                    if (accept) {continue;}

                    accept = padding2(w);
                    if (accept) {continue;}

                    padding3(w);

                }
            }

        });
    }

    private void padding3(Word w) {
        String code = w.getCode();
        String finalCode = code + "v" + "z";
        w.setCode(finalCode);
        addToResult(w);
        gv.increaseCodeLengthCounter(finalCode.length())
          .updateCodeSetCounter(finalCode);
    }

    private boolean padding2(Word w) {
        String code = w.getCode();
        for (char c= 'a'; c<='z'; c++) {
            String newCode = code + 'v' + c;
            if (gv.isNotInCodeSet(newCode)) {
                w.setCode(newCode);
                addToResult(w);
                gv.increaseCodeLengthCounter(newCode.length())
                  .updateCodeSetCounter(newCode);
                return true;
            }
        }
        return false;
    }

    private boolean padding(Word w) {
        String[] padding = {"e", "i", "o", "u"};
        String code = w.getCode();
        boolean accept = false;
        for (String s : padding) {
            String newCode = code + s;
            if (gv.isNotInCodeSet(newCode)) {
                accept = true;
                w.setCode(newCode);
                addToResult(w);
                gv.increaseCodeLengthCounter(newCode.length())
                  .updateCodeSetCounter(newCode);
                break;
            }
        }
        return accept;
    }

    private void addToResult(Word w) {
        String hz = w.getWord();
        if (gv.isIn500Set(hz) || gv.isIn1000Set(hz) || gv.isIn2000Set(hz)) {
            gv.addToResult(w);
        } else if (gv.isInBig5Common(hz)) {
            gv.addToResult2(w);
        } else if (gv.isInBig5(hz)) {
	        gv.addToResult3(w);
        } else if (gv.isInBig5Hkscs(hz)) {
	        gv.addToResult4(w);
        } else if (gv.isInGB2312Set(hz)) {
	        gv.addToResult5(w);
        } else if (gv.isInJapanese(hz)) {
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
		List<Word> words = joinList(result, result2, result3
//               ,result4, result5, result6, result7
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
