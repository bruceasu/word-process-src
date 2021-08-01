package me.asu.word.shortern;

import static me.asu.cli.command.cnsort.Orders.searchSimplifiedOrder;

import java.util.*;
import lombok.extern.slf4j.Slf4j;
import me.asu.word.Word;

@Slf4j
public class MergedMakeShort2 {

    GlobalVariables gv   = new GlobalVariables();
    Options         opts = new Options();

    public MergedMakeShort2() {
        opts.setGv(gv);
    }

    public Map<String, List<Word>> makeSort(List<Word> lines, List<String> oneSet) {
        processOneSet(oneSet);
        group(lines);

        processGroups();
        postProcess();

        return makeResult();
    }

    protected void processOneSet(List<String> oneSet) {
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

    private void group(List<Word> lines) {
        if (lines == null) {
            return;
        }
        log.info("Group {} lines into groups.", lines.size());
        lines.sort(Word::compare);
        for (int i = 0; i < lines.size(); i++) {
            Word w = lines.get(i);
            //gv.increaseCode3SetCounter(w.getCode() + w.getCodeExt().substring(0, 1));
            if (gv.isInSingleSet(w.getWord())) {
                w.setLevel(2);
                String code = w.getCode() + w.getCodeExt();
                w.setCode(code);
                w.setCodeExt("");
                gv.addToFull(w);
            } else if (gv.isIn500Set(w.getWord())) {
                w.setLevel(10);
                gv.getGroup500().add(w);
            } else if (gv.isIn1600Set(w.getWord())) {
                w.setLevel(20);
                gv.addToG1600(w);
            } else if (gv.isIn3800Set(w.getWord())) {
                w.setLevel(30);
                gv.addToG3800(w);
            } else if (gv.isIn4200Set(w.getWord())) {
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
        log.info("group500: {}", gv.group500.size());
        log.info("group1600: {}", gv.group1600.size());
        log.info("group3800: {}", gv.group3800.size());
        log.info("group4200: {}", gv.group4200.size());
        log.info("groupOther: {}", gv.groupOther.size());
    }

    private void processGroups() {
        log.info("Processing groups ...");
        // 一级汉字

        // 1. 前五百字
        process500(gv.group500);
        // 2. 前千五百，能有三码，必有三码，不能则四码必在前。
        process1500(gv.group1600);
        // 3. 四字中唯一的，若三码亦唯一，则取3码，否则取四码
        // 4. 四码字中，有多字的，按字频排序，取首字，若能取三码能取则。
        processOther();
    }

    private void processOther() {
        SortedMap<String, List<Word>> m    = new TreeMap<>();
        List<List<Word>>              list = new LinkedList<>();
        list.add(gv.group3800);
        list.add(gv.group4200);
        list.add(gv.groupOther);
        list.forEach(g -> {
            g.forEach(w -> {
                String code    = w.getCode();
                String codeExt = w.getCodeExt();
                String code3   = code + codeExt.charAt(0);
                if (m.containsKey(code3)) {
                    m.get(code3).add(w);
                } else {
                    List<Word> words = new ArrayList<>();
                    words.add(w);
                    m.put(code3, words);
                }
            });
        });

        m.forEach((k, v) -> {
            Collections.sort(v, Word::compare);
            Word w     = v.get(0);
            int  start = 0;
            if (gv.isNotInCodeSet(k)) {
                Word newWord = w.clone();
                newWord.setCode(k);
                newWord.setCodeExt("");
                gv.increaseCodeLengthCounter(k.length())
                  .updateCodeSetCounter(k);

                String hz = newWord.getWord();
                if (gv.isIn3800Set(hz)) {
                    gv.addToResult(newWord);
                } else if (gv.isIn4200Set(hz)) {
                    gv.addToResult2(newWord);
                } else {
                    gv.addToUncommon(newWord);
                }

                w.setLevel(50 + w.getLevel());
                w.setCode(w.getCode() + w.getCodeExt());
                w.setCodeExt("");
                gv.addToFull(w);
                start = 1;
            }

            for (int i = start; i < v.size(); i++) {
                Word   word = v.get(i);
                String full = word.getCode() + word.getCodeExt();
                word.setCode(full);
                word.setCodeExt("");

                String  hanzi   = word.getWord();
                if (gv.isNotInCodeSet(full)) {
                    if (gv.isIn3800Set(hanzi)) {
                        gv.addToResult(word);
                    } else if( gv.isIn4200Set(hanzi)) {
                        gv.addToResult2(word);
                    }else {
                        gv.addToUncommon(word);
                    }
                } else {
                    if (gv.isIn3800Set(hanzi)|| gv.isIn4200Set(hanzi)) {
                        gv.addToResult2(word);
                    } else {
                        gv.addToUncommon(word);
                    }
                }
                gv.increaseCodeLengthCounter(full.length())
                  .updateCodeSetCounter(full);
            }


        });
    }

    private void process1500(List<Word> group1500) {
        group1500.forEach(w -> {
            String code    = w.getCode();
            String codeExt = w.getCodeExt();
            String code3   = code + codeExt.charAt(0);
            String full    = code + codeExt;

            if (gv.isInCodeSet(code3)) {
                gv.increaseCodeLengthCounter(full.length())
                  .updateCodeSetCounter(full);
                w.setCode(full);
                w.setCodeExt("");
                if (gv.isNotInCodeSet(full)) {
                    gv.addToResult(w);
                } else {
                    gv.addToResult2(w);
                }
                return;
            }

            Word newWord = w.clone();
            newWord.setCode(code3);
            newWord.setCodeExt("");
            gv.increaseCodeLengthCounter(code3.length())
              .updateCodeSetCounter(code3);
            gv.addToResult(newWord);

            w.setLevel(50 + w.getLevel());
            w.setCode(full);
            w.setCodeExt("");
            gv.addToFull(w);


        });
    }

    private void process500(List<Word> group500) {
        group500.forEach(w -> {
            String code    = w.getCode();
            String codeExt = w.getCodeExt();
            String code3   = code + codeExt.charAt(0);
            String full    = w.getCode() + w.getCodeExt();
            if (gv.isNotInCodeSet(code)) {
                Word newWord = w.clone();
                newWord.setCode(code);
                newWord.setCodeExt("");
                gv.increaseCodeLengthCounter(code.length())
                  .updateCodeSetCounter(code);
                gv.addToResult(newWord);

                w.setLevel(50 + w.getLevel());
                w.setCode(full);
                gv.addToFull(w);
                return;
            }

            if (gv.isNotInCodeSet(code3)) {
                Word newWord2 = w.clone();
                newWord2.setCode(code3);
                newWord2.setCodeExt("");
                gv.increaseCodeLengthCounter(code3.length())
                  .updateCodeSetCounter(code3);
                gv.addToResult(newWord2);

                w.setLevel(50 + w.getLevel());
                w.setCode(full);
                gv.addToFull(w);
                return;
            }

            w.setCode(full);
            gv.increaseCodeLengthCounter(full.length())
              .updateCodeSetCounter(full);
            if(gv.isNotInCodeSet(full)) {
                gv.addToResult(w);
            } else {
                gv.addToResult2(w);
            }

        });
    }


    private void postProcess() {
        //  fullProcess();
        printCounter("Post process done!");
    }

    private Map<String, List<Word>> makeResult() {
        Map<String, List<Word>> results = new HashMap<>();
        results.put("result", gv.result);
        results.put("result2", gv.result2);
        results.put("full", gv.full);
        results.put("uncommon", gv.uncommon);
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
            Word   w    = iter.next();
            String code = w.getCode();
            if (gv.isNotInCodeSet(code) && gv.isIn4200Set(w.getWord())) {
                w.setCode(code);
                w.setCodeExt("");
                w.setLevel(200);
                gv.increaseCodeLengthCounter(code.length())
                  .addCodeSetCounter(code)
                  .addToResult2(w);
                iter.remove();
            } else {
                w.setCode(code);
                w.setCodeExt("");
                w.setLevel(300);
            }
        }
    }


}
