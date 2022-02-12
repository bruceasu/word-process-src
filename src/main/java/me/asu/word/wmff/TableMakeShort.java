package me.asu.word.wmff;


import static me.asu.cli.command.cnsort.Orders.searchSimplifiedOrder;

import java.io.IOException;
import java.util.*;
import lombok.extern.slf4j.Slf4j;
import me.asu.word.Word;
import me.asu.word.shortern.GlobalVariables;

/**
 * Created by suk on 2019/6/4.
 */
@Slf4j
public class TableMakeShort {

    GlobalVariables gv = new GlobalVariables();

    public Map<String, List<Word>> makeSort(List<String> lines, List<String> oneSet)
    throws IOException {
        processOneSet(oneSet);
        group(lines);
        processGroups(gv.getGroup500(), 0, 1, true);
        processGroups(gv.getGroup1600(), 2, 1, true);
        processGroups(gv.getGroup3800(), 4, 1, true);
        processGroups(gv.getGroup4200(), 6, 1, true);
        processOther();
        processFull();
        gv.printCounter();
        return makeResult();
    }

    private void processOneSet(List<String> oneSet) {
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
            gv.updateCodeSetCounter(kv[1]).increaseCodeLengthCounter(1).addToResult(w)
              .addToSingle(kv[0]);
        }
    }

    private void group(List<String> lines) {
        for (String line : lines) {
            String trim = line.trim();
            if (trim.isEmpty() || trim.startsWith("#")) {
                continue;
            }
            String[] kv = trim.split("\\s+");
            if (kv.length < 2) {
                continue;
            }
            Word w = new Word();
            w.setWord(kv[0]);
            w.setCode(kv[1]);
            w.setOrder(searchSimplifiedOrder(kv[0]));

            if (gv.isInSingleSet(w.getWord())) {
                w.setLevel(2);
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
        }
    }

    private void processGroups(List<Word> group, int offset, int dup, boolean allowCode3) {
        for (Word w : group) {
            try {
                String code2 = w.getCode().substring(0, 2);
                String code3 = w.getCode().substring(0, 3);
                if (gv.isNotInCodeSet(code2)) {
                    int level = 10 + offset;
                    addNewCode(w, code2, level);
                } else if (gv.isInCodeSet(code2) && gv.getCodeSetCount(code2) < dup) {
                    int level = 11 + offset;
                    addNewCode(w, code2, level);
                } else if (gv.isNotInCodeSet(code3)) {
                    int level = 20 + offset;
                    addNewCode(w, code3, level);
                } else if (allowCode3 && gv.isInCodeSet(code3) && gv.getCodeSetCount(code3) < dup) {
                    int level = 21 + offset;
                    addNewCode(w, code3, level);
                } else {
                    if (gv.isIn1600Set(w.getWord())) {
                        System.out.printf("$ddcmd(\"%s\", \"%s[]\")  %s\t%s%n", w.getWord(),
                                w.getWord(), w.getCode() + w.getCodeExt(), w);
                    }
                    w.setLevel(50 + offset);
                    gv.addToGroupOther(w);
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                System.out.println("w = " + w);
            }
        }
    }

    private void processOther() {
        Map<String, List<Word>> groupBySyllables = gv.groupSyllablesForOther();
        for (Map.Entry<String, List<Word>> entry : groupBySyllables.entrySet()) {
            String code = entry.getKey();
            List<Word> ws = entry.getValue();
            Collections.sort(ws);
            String code3 = code.substring(0, 3);
            if (ws.size() == 1) {
                Word w = ws.get(0);
                if (gv.isNotInCodeSet(code3)) {
                    Word newWord = w.clone();
                    newWord.setCode(code3);
                    gv.addCodeSetCounter(code3)
                      .increaseCodeLengthCounter(code3.length())
                      .addToResult(newWord)
                      .addToFull(w);
                } else {
                    gv.increaseCodeLengthCounter(code.length());
                    w.setCode(code);

                    if (gv.isNotInCodeSet(code)) {
                        gv.addCodeSetCounter(code)
                          .increaseCodeLengthCounter(code.length());
                        gv.addToResult(w);
                    } else {
                        if (gv.isInGB2312Set(w.getWord())) {
                            gv.addToResult6(w);
                        } else {
                            gv.addToResult(w);
                        }
                    }
                }
            } else {
                int start = 0;
                if (gv.isNotInCodeSet(code3)) {
                    Word w = ws.get(0);
                    Word newWord = w.clone();
                    newWord.setCode(code3);
                    gv.addCodeSetCounter(code3)
                      .increaseCodeLengthCounter(code3.length())
                      .addToResult(newWord)
                      .addToFull(w);
                    start = 1;
                }

                for (int i = start; i < ws.size(); i++) {
                    Word w = ws.get(i);
                    if (gv.isNotInCodeSet(code)) {
                        gv.addCodeSetCounter(code)
                          .increaseCodeLengthCounter(code.length());
                        gv.addToResult(w);
                    } else {
                        if (gv.isInGB2312Set(w.getWord())) {
                            w.setLevel(100);
                            gv.addCodeSetCounter(code)
                              .increaseCodeLengthCounter(code.length())
                              .addToResult(w);
                        } else {
                            w.setLevel(110);
                            gv.addCodeSetCounter(code)
                              .increaseCodeLengthCounter(code.length())
                              .addToResult6(w);
                        }

                    }
                }
            }
        }
    }

    private void processFull() {
        Iterator<Word> iter = gv.getFull().iterator();
        while (iter.hasNext()) {
            Word w = iter.next();
            String code = w.getCode();
            gv.increaseCodeLengthCounter(code.length());
            if (gv.isNotInCodeSet(code)) {
                w.setLevel(200);
//                gv.updateCodeSetCounter(code)
//                  .increaseCodeLengthCounter(code.length())
//                  .addToResult(w);
//                iter.remove();
            } else {
                w.setLevel(300);
            }
        }
    }

    private Map<String, List<Word>> makeResult() {
        Map<String, List<Word>> results = new HashMap<>();
        results.put("result", gv.getResult());
        results.put("full", gv.getFull());
        results.put("uncommon", gv.getResult6());
        return results;
    }

    void addNewCode(Word w, String code, int level) {
        w.setLevel(level);
        Word newWord = w.clone();
        newWord.setCode(code);
        newWord.setCodeExt("");
        gv.increaseCodeLengthCounter(code.length())
          .addToResult(newWord)
          .addToFull(w)
          .updateCodeSetCounter(code);
    }
}
