package me.asu.word.shortern;

import static me.asu.cli.command.cnsort.Orders.searchSimplifiedOrder;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.*;
import me.asu.word.Word;
import org.slf4j.Logger;

/**
 * 1. 一简
 * 2. 二简（前500)
 * 3. 一般常用字三简
 * 4. 一般常用字全码
 * 5. 生僻字填充三码
 * 6. 生僻字全码
 */
public class MergedMakeShort
{

	private static final Logger log = getLogger(MergedMakeShort.class);

	GlobalVariables gv   = new GlobalVariables();
	Options         opts = new Options();

	public MergedMakeShort()
	{
		opts.setGv(gv);
	}

	public Map<String, List<Word>> makeSort(List<Word> lines, List<String> oneSet)
	{
		processOneSet(oneSet);
		group(lines);

		processCommonGroups();
		otherGroupProcess();
		postProcess();

		return makeResult();
	}

	protected void processOneSet(List<String> oneSet)
	{
		for (String str : oneSet) {
			String[] kv = str.split("\\s+");
			if (kv.length < 2) {
				continue;
			}
			Word w = new Word();
			w.setWord(kv[0]);
			w.setCode(kv[1]);
			w.setLevel(1);
			w.setOrder(searchSimplifiedOrder(kv[0]));
			gv.addToResult(w);
			gv.updateCodeSetCounter(kv[1])
			  .increaseCodeLengthCounter(kv[1].length())
			  .addToSingle(kv[0]);
		}
	}

	private void group(List<Word> lines)
	{
		if (lines == null) {
			return;
		}
		log.info("Group {} lines into groups.", lines.size());
		lines.sort(Word::compare);
		for (int i = 0; i < lines.size(); i++) {
			Word w = lines.get(i);
			if (gv.isInSingleSet(w.getWord())) {
				w.setLevel(2);
				String code = w.getCode() + w.getCodeExt();
				w.setCode(code);
				w.setCodeExt("");
				gv.addToFull(w);
			} else if (gv.isIn500Set(w.getWord())) {
				gv.getGroup500().add(w);
			} else if (gv.isIn1600Set(w.getWord())) {
				gv.addToG1600(w);
			} else if (gv.isIn3800Set(w.getWord())) {
				gv.addToG3800(w);
			} else if (gv.isIn4200Set(w.getWord())) {
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
		log.info("group500: {}", gv.group500.size());
		log.info("group1600: {}", gv.group1600.size());
		log.info("group3800: {}", gv.group3800.size());
		log.info("group4200: {}", gv.group4200.size());
		log.info("groupOther: {}", gv.groupOther.size());
	}

	private void processCommonGroups()
	{
		log.info("Processing groups ...");
		Options opts = new Options().globalVariables(gv);
		opts.predicate(1, w -> false)
		    .predicate(2, w -> false)
		    .predicate(3, w -> false)
		    .predicate(4, w -> false);
		//    .function(4, w -> w.getCode() + " " + w.getCodeExt());

		opts.predicate(1, w -> gv.isIn500Set(w.getWord())
				&& gv.getCodeSetCount(w.getCode().substring(0, 1)) < 1)
		    .function(1, w -> w.getCode().substring(0, 1));
		opts.predicate(2, w -> {
			String code = w.getCode();
			return gv.isNotInCodeSet(code);
		}).function(2, Word::getCode);
		opts.predicate(3, w -> {
			String code3 = w.getCode() + w.getCodeExt().substring(0, 1);
			boolean notIn = gv.isNotInCodeSet(code3);
//			boolean inLevel = gv.isIn1600Set(w.getWord());
//			return (notIn && inLevel);
			return notIn;
		}).function(3, w -> w.getCode() + w.getCodeExt().substring(0, 1));
		opts.predicate(4, w -> {
//            return false;
//            String code = w.getCode() + w.getCodeExt();
//            boolean notIn = gv.isNotInCodeSet(code);
//            final boolean in4200Set = gv.isIn4200Set(w.getWord());

			boolean inLevel = gv.isIn1600Set(w.getWord());
			return inLevel;
		}).function(4, w -> w.getCode() + w.getCodeExt());

		// 一级汉字
		List<Word> group500 = new ArrayList<>(gv.group500);
		opts.group(group500);
		new GroupProcessor(opts).processGroups();

		List<Word> group1600 = new ArrayList<>(gv.group1600);
		opts.group(group1600);
		new GroupProcessor(opts).processGroups();

		List<Word> group3800 = new ArrayList<>(gv.group3800);
		opts.group(group3800);
		new GroupProcessor(opts).processGroups();

		List<Word> group4200 = new ArrayList<>(gv.group4200);
		opts.group(group4200);
		new GroupProcessor(opts).processGroups();
		printCounter("First round done.");
	}

	private void otherGroupProcess()
	{
		Map<String, List<Word>> groupBySyllables = gv.groupSyllablesForOther();
		for (Map.Entry<String, List<Word>> entry : groupBySyllables.entrySet()) {
			String code = entry.getKey();
			List<Word> ws = entry.getValue();
			Collections.sort(ws);
			String code3 = code.substring(0, 3);
//			if (ws.size() == 1) {
//                for (int i = 0; i < ws.size(); i++) {
//                    Word w = ws.get(i);
//                    if (gv.isIn3800Set(w.getWord())) {
//                        w.setCode(code);
//                        w.setCodeExt("");
//                        gv.updateCodeSetCounter(code)
//                          .addToResult(w)
//                          .increaseCodeLengthCounter(code.length());
//                    } else if (gv.isIn4200Set(w.getWord())) {
//                        w.setCode(code);
//                        w.setCodeExt("");
//                        gv.updateCodeSetCounter(code)
//                          .addToResult2(w)
//                          .increaseCodeLengthCounter(code.length());
//                    } else {
//                        w.setCode(code);
//                        w.setCodeExt("");
//                        gv.updateCodeSetCounter(code)
//                          .increaseCodeLengthCounter(code.length())
//                          .addToUncommon(w);
//                    }
//                }
//			} else {
				for (int i = 0; i < ws.size(); i++) {
					Word w = ws.get(i);
					Word newWord = w.clone();
					newWord.setCode(code3);
					newWord.setCodeExt("");
					//gv.addRecheck(newWord);
					String hz = w.getWord();
					if (gv.isIn3800Set(hz)) {
						if (gv.isNotInCodeSet(code3)) {
							w.setCode(code);
							w.setCodeExt("");
							gv.updateCodeSetCounter(code3)
							  .addToResult(newWord)
							  .increaseCodeLengthCounter(code3.length())
							  .addToFull(w);
						} else if (gv.isNotInCodeSet(code)) {
							w.setCode(code);
							w.setCodeExt("");
							gv.updateCodeSetCounter(code)
							  .addToResult(w)
							  .increaseCodeLengthCounter(code.length());
						} else {
							w.setCode(code);
							w.setCodeExt("");
							gv.updateCodeSetCounter(code)
							  .addToResult2(w)
							  .increaseCodeLengthCounter(code.length());
						}
					} else if (gv.isIn4200Set(hz)) {
						if (gv.isNotInCodeSet(code3)) {
							w.setCode(code);
							w.setCodeExt("");
							gv.updateCodeSetCounter(code3)
							  .addToResult2(newWord)
							  .increaseCodeLengthCounter(code3.length())
							  .addToFull(w);
						} else if (gv.isNotInCodeSet(code)) {
							w.setCode(code);
							w.setCodeExt("");
							gv.updateCodeSetCounter(code)
							  .addToResult2(w)
							  .increaseCodeLengthCounter(code.length());
						} else {
							w.setCode(code);
							w.setCodeExt("");
							gv.updateCodeSetCounter(code)
							  .addToResult2(w)
							  .increaseCodeLengthCounter(code.length());
						}
					} else {
						if (gv.isNotInCodeSet(code3)) {
							w.setCode(code);
							w.setCodeExt("");
							gv.updateCodeSetCounter(code3)
							  .addToResult6(newWord)
							  .increaseCodeLengthCounter(code3.length())
							  .addToFull(w);
						} else {
							w.setCode(code);
							w.setCodeExt("");
							gv.updateCodeSetCounter(code)
							  .increaseCodeLengthCounter(code.length())
							  .addToResult6(w);
						}
					}
				}
//			}


		}
		printCounter("Second round done.");
	}

	private void postProcess()
	{
		//fullProcess();
		printCounter("Post process done!");
	}

	private Map<String, List<Word>> makeResult()
	{
		Map<String, List<Word>> results = new HashMap<>();
		results.put("result", gv.result);
		results.put("result2", gv.result2);
		results.put("full", gv.full);
		results.put("uncommon", gv.result6);
		return results;
	}

	private void printCounter(String x)
	{
		log.info(x);
		gv.printCounter();
	}

	private void fullProcess()
	{
		log.info("Processing full ...");
		Iterator<Word> iter = gv.full.iterator();
		while (iter.hasNext()) {
			Word w = iter.next();
			String code = w.getCode();
			if (gv.isNotInCodeSet(code)) {
				w.setCode(code);
				w.setCodeExt("");
				w.setLevel(200);
				gv.increaseCodeLengthCounter(code.length())
				  .addCodeSetCounter(code);
				if (gv.isIn4200Set(w.getWord())) {
					gv.addToResult2(w);
				} else {
					gv.addToResult6(w);
				}
				iter.remove();
			} else {
				w.setCode(code);
				w.setCodeExt("");
				w.setLevel(300);
			}
		}
	}
}
