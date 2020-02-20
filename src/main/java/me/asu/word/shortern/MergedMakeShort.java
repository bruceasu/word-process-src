package me.asu.word.shortern;

import static me.asu.cli.command.Orders.searchSimplifiedOrder;

import java.util.*;
import lombok.extern.slf4j.Slf4j;
import me.asu.word.Word;

@Slf4j
public class MergedMakeShort
{

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
            gv.updateCodeSetCounter(kv[1]).increaseCodeLengthCounter(kv[1].length()).addToResult(w)
              .addToSingle(kv[0]);
        }
    }

    private void group(List<Word> lines)
    {
        if (lines == null) {
            return;
        }
        log.info("Group {} lines into groups.", lines.size());
        for (int i = 0; i < lines.size(); i++) {
            Word w = lines.get(i);
            gv.increaseCode3SetCounter(w.getCode() + w.getCodeExt().substring(0, 1));
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
            } else if (gv.isIn5700Set(w.getWord())) {
                gv.addToG5700(w);
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
        log.info("group5700: {}", gv.group5700.size());
        log.info("groupOther: {}", gv.groupOther.size());
    }

    private void processCommonGroups()
    {
        log.info("Processing groups ...");
        Options opts = new Options().globalVariables(gv);
        opts.predicate(1, w -> gv.isNotInCodeSet(w.getCode().substring(0, 1)))
            .function(1, w -> w.getCode().substring(0, 1));
        opts.predicate(2, w -> gv.isNotInCodeSet(w.getCode().substring(0, 2)))
            .function(2, Word::getCode);
        opts.predicate(3, w -> {
            //boolean in500 = gv.isIn500Set(w.getWord());
            boolean notIn = gv.isNotInCodeSet(w.getCode() + w.getCodeExt().substring(0, 1));
            //return in500 || notIn;
            return notIn;
        }).function(3, w -> w.getCode() + w.getCodeExt().substring(0, 1));

        opts.predicate(4, w -> {
            boolean in3800 = gv.isIn3800Set(w.getWord());
            boolean notIn  = gv.isNotInCodeSet(w.getCode() + w.getCodeExt());
            return in3800 && notIn;
        }).function(4, w -> w.getCode() + w.getCodeExt());

        List<Word> group500 = new ArrayList<>(gv.group500);
        opts.group(group500);
        new GroupProcessor(opts).processGroups();

        List<Word> group1600 = new ArrayList<>(gv.group1600);
        opts.group(group1600);
        new GroupProcessor(opts).processGroups();

        List<Word> group3800 = new ArrayList<>(gv.group3800);
        opts.group(group3800);
        new GroupProcessor(opts).processGroups();

        //        opts.predicate(3, w -> false);
        List<Word> group5700 = new ArrayList<>(gv.group5700);
        opts.group(group5700);
        new GroupProcessor(opts).processGroups();
        printCounter("First round done.");
    }

    private void otherGroupProcess()
    {
        Map<String, List<Word>> groupBySyllables = gv.groupSyllablesForOther();
        for (Map.Entry<String, List<Word>> entry : groupBySyllables.entrySet()) {
            String     code = entry.getKey();
            List<Word> ws   = entry.getValue();
            Collections.sort(ws);
            String code3 = code.substring(0, 3);
            int    start = 0;
            for (int i = start; i < ws.size(); i++) {
                Word w = ws.get(i);
                if (gv.isNotInCodeSet(code3)) {
                    w.setCode(code);
                    w.setCodeExt("");
                    Word newWord = w.clone();
                    newWord.setCode(code3);
                    newWord.setCodeExt("");
                    gv.updateCodeSetCounter(code3)
                      .addToResult2(newWord)
                      .increaseCodeLengthCounter(code3.length())
                      .addToFull(w);

                    //                } else if (gv.isNotInCodeSet(code)) {
                    //                    w.setCode(code);
                    //                    w.setCodeExt("");
                    //                    gv.updateCodeSetCounter(code)
                    //                      .addToResult(w)
                    //                      .increaseCodeLengthCounter(code.length());
                } else if (gv.isIn5700Set(w.getWord())) {
                    w.setCode(code);
                    w.setCodeExt("");
                    gv.updateCodeSetCounter(code)
                      .addToResult2(w)
                      .increaseCodeLengthCounter(code.length());
                } else {
                    w.setCode(code);
                    w.setCodeExt("");
                    gv.updateCodeSetCounter(code).addToUncommon(w);
                }
            }
        }
        printCounter("Second round done.");
    }

    private void postProcess()
    {
        //        fullProcess();
        printCounter("Third round.");
    }

    private Map<String, List<Word>> makeResult()
    {
        Map<String, List<Word>> results = new HashMap<>();
        results.put("result", gv.result);
        results.put("result2", gv.result2);
        results.put("full", gv.full);
        results.put("uncommon", gv.uncommon);
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
            Word   w    = iter.next();
            String code = w.getCode();
            if (gv.isNotInCodeSet(code)
                //&& gv.isIn3800Set(w.getWord())
            ) {
                w.setCode(code);
                w.setCodeExt("");
                w.setLevel(200);
                gv.increaseCodeLengthCounter(code.length()).addCodeSetCounter(code).addToResult(w);
                iter.remove();
            } else {
                w.setCode(code);
                w.setCodeExt("");
                w.setLevel(300);
            }
        }
    }


}