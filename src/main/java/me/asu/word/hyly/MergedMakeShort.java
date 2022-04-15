package me.asu.word.hyly;

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
public class MergedMakeShort {

    GlobalVariables gv         = new GlobalVariables();
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
            //gv.increaseCode3SetCounter(w.getCode() + w.getCodeExt().substring(0, 1));
            String hz = w.getWord();
            if (gv.isInSingleSet(hz)) {
//                String code1 = w.getCode().substring(0, 1);
//                String code2 = w.getCode().substring(0, 2);
//                String code3 = w.getCode().substring(0, 3);
//                boolean found = false;
//                String[] col = {code1, code2, code3};
//                for (String s : col) {
//                    if (oneSetColl.contains(hz + "\t" + s)) {
//                        found = true;
//                        break;
//                    }
//                }
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

            if (gv.isIn500Set(hz)) {
                w.setLevel(10);
                gv.getGroup500().add(w);
            } else if (gv.isIn1000Set(hz)) {
                w.setLevel(20);
                gv.addToG1600(w);
            } else if (gv.isIn2000Set(hz)) {
                w.setLevel(30);
                gv.addToG3800(w);
            } else if (gv.isIn4000Set(hz)) {
                w.setLevel(40);
                gv.addToG4200(w);
            } else {
                w.setLevel(90);
                gv.addToGroupOther(w);
            }
            if (i % 1000 == 0) {
                log.info("Processed {} lines.", i);
            }
        }
        log.info("Processed {} lines.", lines.size());
        log.info("group500: {}", gv.getGroup500().size());
        log.info("group1000: {}", gv.getGroup1000().size());
        log.info("group2000: {}", gv.getGroup2000().size());
        log.info("group4000: {}", gv.getGroup4000().size());
        log.info("groupOther: {}", gv.getGroupOther().size());
    }

    private void processGroups() {
        log.info("Processing groups ...");
        processGroupLevel1(gv.getGroup500(), gv.getGroup1000(),
                gv.getGroup2000());
        List<Word> remain = new ArrayList<>(gv.getRemain());
        remain.clear();
        processGroupLevel2(remain, gv.getGroup4000());
        processOtherGroup(gv.getGroupOther());

    }

    private void processGroupLevel1(List<Word>... wordList) {
        List<Word> list = joinList(wordList);
        processOneAndTwo(list);
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

    private void processGroupLevel2(List<Word>... wordList) {
        List<Word> list = joinList(wordList);

        processOneAndTwo(list);
        List<Word> remain = gv.getRemain();
        Map<String, Set<Word>> m = new TreeMap<>();
        for (Word w : remain) {
            String code = w.getCode().substring(0, 3);
            w.setCodeExt("");
            m.computeIfAbsent(code, k -> new HashSet<>());
            m.get(code).add(w);
        }
        remain.clear();
        m.forEach((k, l) -> {
            List<Word> wl = new ArrayList<>(l.size());
            wl.addAll(l);
            wl.sort(Word::compareTo);
            int size = wl.size();
            if (size == 1) {
                Word w = wl.get(0);
                Word clone = w.clone();
                w.setCode(k);
                addToResult(w);
                gv.increaseCodeLengthCounter(k.length())
                  .updateCodeSetCounter(k);
                clone.setCodeExt("");
                gv.addToFull(clone);
//            } else if (size == 2) {
//                for (int i = 0; i < size; i++) {
//                    Word w = wl.get(i);
//                    Word clone = w.clone();
//                    w.setCode(k);
//                    addToResult(w);
//                    gv.increaseCodeLengthCounter(k.length())
//                      .updateCodeSetCounter(k);
//                    clone.setCodeExt("");
//                    gv.addToFull(clone);
//                }
            } else {
                Word w = wl.get(0);
                Word clone = w.clone();
                w.setCode(k);
                addToResult(w);
                gv.increaseCodeLengthCounter(k.length())
                  .updateCodeSetCounter(k);
                clone.setCodeExt("");
                gv.addToFull(clone);
                for (int i = 1; i < size; i++) {
                    w = wl.get(i);
                    String code = w.getCode();
                    addToResult(w);
                    gv.increaseCodeLengthCounter(code.length())
                      .updateCodeSetCounter(code);
                }
            }
        });
    }

    private void processOtherGroup(List<Word>... wordList) {
        List<Word> wl = joinList(wordList);
        for (int i = 0; i < wl.size(); i++) {
            Word w = wl.get(i);
            Word clone = w.clone();
            String code = w.getCode();
            String code3 = code.substring(0, 3);
            if (gv.isNotInCodeSet(code3)) {
                w.setCode(code3);
                addToResult(w);
                gv.increaseCodeLengthCounter(code3.length())
                  .updateCodeSetCounter(code3);
                clone.setCodeExt("");
                gv.addToFull(clone);
            } else {
                addToResult(w);
                gv.increaseCodeLengthCounter(code.length())
                  .updateCodeSetCounter(code);
            }
        }
    }

    private void addToResult(Word w) {
        String hz = w.getWord();
        if (gv.isIn500Set(hz) || gv.isIn1000Set(hz) || gv.isIn2000Set(hz)) {
            gv.addToResult(w);
        } else if (gv.isIn4000Set(hz)) {
            gv.addToResult2(w);
        } else if (gv.isInGB2312Set(hz)) {
	        gv.addToResult3(w);
        } else if (gv.isInBig5(hz)) {
	        gv.addToResult4(w);
        } else if (gv.isInJapanese(hz)) {
	        gv.addToResult5(w);
        } else if (gv.isInBig5Hkscs(hz)) {
            gv.addToResult6(w);
        } else {
	        gv.addToResult7(w);
        }
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
//                ,result4, result5, result6, result7
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
