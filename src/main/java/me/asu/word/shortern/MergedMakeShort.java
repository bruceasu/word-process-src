package me.asu.word.shortern;

import static me.asu.cli.command.cnsort.Orders.searchSimplifiedOrder;

import java.util.*;
import lombok.extern.slf4j.Slf4j;
import me.asu.word.Word;

/**
 * 用于顶功模式
 */
@Slf4j
public class MergedMakeShort {

	GlobalVariables gv         = new GlobalVariables();
	Options         opts       = new Options();
	Set<String>     oneSetColl = new HashSet<>();

	public MergedMakeShort() {
		opts.setGv(gv);
	}

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
			} else {
				w.setLevel(90);
				gv.addToGroupOther(w);
			}
			if (i % 1000 == 0) {
				log.info("Processed {} lines.", i);
			}
		}
		log.info("Processed {} lines.", lines.size());
		log.info("group500: {}", gv.group1.size());
		log.info("group1000: {}", gv.group2.size());
		log.info("group2000: {}", gv.group3.size());
		log.info("group4000: {}", gv.group4.size());
		log.info("groupOther: {}", gv.groupOther.size());
	}

	private void processGroups() {
		log.info("Processing groups ...");
		processGroupLevel1(gv.group1);
		processGroupLevel2(gv.group2);
		processGroup(gv.group3, gv.group4, gv.groupOther);

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
			String code = w.getCode();
			String ext = w.getCodeExt();
			String hz = w.getWord();
			String code1 = code.substring(0, 1);
			String code3 = code + ext.substring(0, 1);
			String full = code + ext;
			if (gv.isNotInCodeSet(code)) {
				w.setCodeExt("");
				addToResult(w);
				continue;
			}

			String[] ls = {code1, code, code3};
			boolean accept = false;
			for (String l : ls) {
				if (gv.getCodeSetCount(l) < 2) {
					accept = procDupCode(w, l, 1);
					if(accept) {
						addToResult(w);
						Word clone = w.clone();
						clone.setCode(full);
						gv.addToFull(clone);
						break;
					}

				}
			}
			if (accept) continue;
			// add full
//            w.setCode(full);
//            w.setCodeExt("");
//            gv.increaseCodeLengthCounter(full.length())
//              .updateCodeSetCounter(full);
//            addToResult(w);
			gv.addToRemain(w);
		}
	}

	private void processGroupLevel2(List<Word>... wordList) {
		List<Word> list = new LinkedList<>();
		for (List<Word> wList : wordList) {
			list.addAll(wList);
		}
		Collections.sort(list, Word::compare);
		for (Word w : list) {
			String code = w.getCode();
			String ext = w.getCodeExt();
			String hz = w.getWord();
			String code1 = code.substring(0, 1);
			String code3 = code + ext.substring(0, 1);
			String full = code + ext;
			if (gv.isNotInCodeSet(code)) {
				w.setCodeExt("");
				addToResult(w);
				continue;
			}

			String[] ls = {code1, code, code3};
			boolean accept = false;
			for (String l : ls) {
				if (gv.getCodeSetCount(l) < 2) {
					accept = procDupCode(w, l, 1);
					if(accept) {
						addToResult(w);
						Word clone = w.clone();
						clone.setCode(full);
						gv.addToFull(clone);
						break;
					}

				}
			}
			if (accept) continue;
			// add full
//            w.setCode(full);
//            w.setCodeExt("");
//            gv.increaseCodeLengthCounter(full.length())
//              .updateCodeSetCounter(full);
//            addToResult(w);
			gv.addToRemain(w);
		}
	}

	private void processGroup(List<Word>... wordList) {
		List<Word> list = new LinkedList<>();
		for (List<Word> wList : wordList) {
			list.addAll(wList);
		}
		Collections.sort(list, Word::compare);

		Collections.sort(list, Word::compare);
		for (int i = 0; i < list.size(); i++) {
			Word w = list.get(i);
			String code = w.getCode();
			String ext = w.getCodeExt();
			String code3 = code + ext.substring(0, 1);
			String full = code +ext;
			if (gv.isNotInCodeSet(code)) {
				Word clone = w.clone();
				w.setCodeExt("");
				addToResult(w);
				gv.increaseCodeLengthCounter(code.length())
				  .updateCodeSetCounter(code);
				clone.setCodeExt("");
				clone.setCode(code+ext);
				gv.addToFull(clone);
			} else if (gv.isNotInCodeSet(code3)) {
				Word clone = w.clone();
				w.setCodeExt("");
				w.setCode(code3);
				addToResult(w);
				gv.increaseCodeLengthCounter(code3.length())
				  .updateCodeSetCounter(code3);

				clone.setCodeExt("");
				clone.setCode(code+ext);
				gv.addToFull(clone);
			} else {
//                w.setCode(full);
//                w.setCodeExt("");
//                addToResult(w);
//                gv.increaseCodeLengthCounter(full.length())
//                  .updateCodeSetCounter(full);

				gv.addToRemain(w);
			}
		}
		List<Word> remain = gv.getRemain();
		Map<String, Set<Word>> m = new TreeMap<>();
		for (Word w : remain) {
			String code = w.getCode();
			String ext = w.getCodeExt();
			String full = code + ext;
			w.setCodeExt("");
			w.setCode(full);
			m.computeIfAbsent(full, k->new HashSet<>());
			m.get(full).add(w);
		}
		m.forEach((k, l)->{
			List<Word> wl = new ArrayList<>(l.size());
			wl.addAll(l);
			wl.sort(Word::compareTo);
			for (int i = 0; i < wl.size(); i++) {
				Word w = wl.get(i);
				if (i == 0) {
					addToResult(w);
					gv.increaseCodeLengthCounter(k.length())
					  .updateCodeSetCounter(k);
					continue;
				}
				for (int j = 0; j < 26; j++) {
					String c = w.getCode() + ((char)(97 + j));
//                        System.out.println("J : " + j + ", Before: "+w);
					if(gv.isNotInCodeSet(c)) {
						w.setCode(c);
//                            System.out.println("Accept: " + w);
						addToResult(w);
						gv.increaseCodeLengthCounter(c.length())
						  .updateCodeSetCounter(c);
						break;
					}
				}
			}
		});
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
		} else if (gv.isInGB2312(hz)) {
			gv.addToResult5(w);
		} else {
			gv.addToResult6(w);
		}
	}

	private boolean procDupCode(Word w, String code, int max) {
		boolean accept = false;
		String newCode = "";
		// 选重
		char[] padding = ";/,.".toCharArray();
		if (gv.isNotInCodeSet(code)) {
			newCode = code;
			gv.increaseCodeLengthCounter(newCode.length())
			  .updateCodeSetCounter(code);
			accept = true;
		} else {
			for (int i = 0; i < padding.length && i < max; i++) {
				newCode = code + padding[i];
				if (gv.isNotInCodeSet(newCode)) {
					accept = true;
					gv.increaseCodeLengthCounter(newCode.length())
					  .updateCodeSetCounter(newCode)
					  .updateCodeSetCounter(code);
					break;
				}
			}


		}
		if (accept) {
			w.setCode(newCode);
			w.setCodeExt("");
		}
		return accept;
	}


	private void postProcess() {
//        fullProcess();
		printCounter("Post process done!");
	}

	private Map<String, List<Word>> makeResult() {
		Map<String, List<Word>> results = new HashMap<>();
		results.put("result", gv.result);
		results.put("result2", gv.result2);
		results.put("result3", gv.result3);
		results.put("result4", gv.result4);
		results.put("result5", gv.result5);
		results.put("result6", gv.result6);
		results.put("result7", gv.result7);
		results.put("full", gv.full);
		return results;
	}

	private void printCounter(String x) {
		log.info(x);
		gv.printCounter();
	}

	private void fullProcess() {
		log.info("Processing full ...");
		Iterator<Word> iter = gv.full.iterator();
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
