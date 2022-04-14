package me.asu.word.hyly;

import static me.asu.cli.command.cnsort.Orders.searchSimplifiedOrder;

import java.util.*;
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
				String code1 = w.getCode().substring(0, 1);
				String code2 = w.getCode();
				String code3 = w.getCode() + w.getCodeExt();
				boolean found = false;
				String[] col = {code1, code2, code3};
				for (String s : col) {
					if (oneSetColl.contains(hz + "\t" + s)) {
						found = true;
						break;
					}
				}
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
		processGroupLevel1(gv.getGroup500(),gv.getGroup1000());
		List<Word> remain = new ArrayList<>(gv.getRemain());
		remain.clear();
		processGroupLevel2(remain, gv.getGroup2000(), gv.getGroup4000());
		processGroup(gv.getGroupOther());

	}

	/**
	 * process group 500
	 */
	private void processGroupLevel1(List<Word>... wordList) {
		List<Word> list = new LinkedList<>();
		for (List<Word> wList : wordList) {
			list.addAll(wList);
		}
		Collections.sort(list, Word::compare);
		for (Word w : list) {
			Word clone = w.clone();
			String code = w.getCode();
			String code1 = code.substring(0, 1);
			String code2 = code.substring(0, 2);
			String code3 = code.substring(0, 3);
			String[] codes = {code1, code2, code3};
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
//                w.setCodeExt("");
//                addToResult(w);
//                gv.increaseCodeLengthCounter(code.length())
//                  .updateCodeSetCounter(code);
				gv.addToRemain(w);
			}
		}
	}

	private void processGroupLevel2(List<Word>... wordList) {
		List<Word> list = new LinkedList<>();
		for (List<Word> wList : wordList) {
			list.addAll(wList);
		}
		Collections.sort(list, Word::compare);

		for (int i = 0; i < list.size(); i++) {
			Word w = list.get(i);
			Word clone = w.clone();
			String code = w.getCode();
			String code1 = code.substring(0,1);
			String code2 = code.substring(0,2);
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
		List<Word> remain = gv.getRemain();
		Map<String, Set<Word>> m = new TreeMap<>();
		for (Word w : remain) {
			String code = w.getCode();
			w.setCodeExt("");
			m.computeIfAbsent(code, k -> new HashSet<>());
			m.get(code).add(w);
		}
		m.forEach((k, l) -> {
			List<Word> wl = new ArrayList<>(l.size());
			wl.addAll(l);
			wl.sort(Word::compareTo);
			int size = wl.size();
			String code3 = k.substring(0,3);
			for (int i = 0; i < size; i++) {
				Word w = wl.get(i);
				Word clone = w.clone();
				if (gv.isNotInCodeSet(code3)) {
					w.setCode(code3);
					addToResult(w);
					gv.increaseCodeLengthCounter(code3.length())
					  .updateCodeSetCounter(code3);
					clone.setCodeExt("");
					gv.addToFull(clone);
				} else  {
					addToResult(w);
					gv.increaseCodeLengthCounter(k.length())
					  .updateCodeSetCounter(k);
				}
			}
		});
	}

	private void processGroup(List<Word>... wordList) {
		List<Word> wl = new LinkedList<>();
		for (List<Word> wList : wordList) {
			wl.addAll(wList);
		}
		Collections.sort(wl, Word::compare);
		for (int i = 0; i < wl.size(); i++) {
			Word w = wl.get(i);
			Word clone = w.clone();
			String code = w.getCode();
			String code3 = code.substring(0,3);
			if (gv.isNotInCodeSet(code3)) {
				w.setCode(code3);
				addToResult(w);
				gv.increaseCodeLengthCounter(code3.length())
				  .updateCodeSetCounter(code3);
				clone.setCodeExt("");
				gv.addToFull(clone);
			} else  {
				addToResult(w);
				gv.increaseCodeLengthCounter(code.length())
				  .updateCodeSetCounter(code);
			}
		}
	}

	private void addToResult(Word w) {
		String hz = w.getWord();
		if (gv.isIn500Set(hz)) {
			gv.addToResult(w);
		} else if (gv.isIn1000Set(hz)) {
			gv.addToResult2(w);
		} else if (gv.isIn2000Set(hz)) {
			gv.addToResult3(w);
		} else if (gv.isIn4000Set(hz)) {
			gv.addToResult4(w);
		} else if (gv.isInGB2312Set(hz)) {
			gv.addToResult5(w);
		}  else if (gv.isInBig5(hz)) {
			gv.addToResult6(w);
		} else if (gv.isInJapanese(hz)) {
			gv.addToResult7(w);
		} else  {
			gv.addToResult6(w);
		}
	}

		private void postProcess() {
//        fullProcess();
		printCounter("Post process done!");
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
