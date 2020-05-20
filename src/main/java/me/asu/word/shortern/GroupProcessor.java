package me.asu.word.shortern;

import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import lombok.extern.slf4j.Slf4j;
import me.asu.word.Word;

@Slf4j
public class GroupProcessor {

    Options         opts;
    GlobalVariables gv;

    public GroupProcessor(Options opts)
    {
        this.opts = opts;
        gv = opts.gv;
    }

    public void processGroups()
    {
        processLen(1);
        processLen(2);
        processLen(3);
        processLen(4);
        processRemain();
    }

    private void processLen(int len)
    {
        List<Word> group = opts.getGroup();
        for (Iterator<Word> iterator = group.iterator(); iterator.hasNext(); ) {
            Word    w   = iterator.next();
            boolean suc = process(w, len);
            if (suc) {
                iterator.remove();
            }
        }
    }

    private void processRemain()
    {
        List<Word> group = opts.getGroup();
        for (Iterator<Word> iterator = group.iterator(); iterator.hasNext(); ) {
            Word w = iterator.next();
            w.setLevel(50 + opts.offset(4));
            gv.addToGroupOther(w);
            iterator.remove();
        }
        log.info("groupOther: {}", gv.groupOther.size());
    }

    private boolean process(Word w, int len)
    {
        try {
            Predicate<Word> prdicate = opts.predicate(len);
            if (prdicate.test(w)) {
                int                    offset   = opts.offset(len);
                int                    level    = len * 10 + offset;
                Function<Word, String> function = opts.function(len);
                if (function == null) {
                   String code = w.getCode().substring(0,1);
                    addNewCode(w, code, level);
                } else {
                    String code = function.apply(w);
                    addNewCode(w, code, level);
                }
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            System.out.println("w = " + w);
            return false;
        }
    }

    public void addNewCode(Word w, String code, int level)
    {
        w.setLevel(level);
        Word newWord = w.clone();
        newWord.setCode(code);
        newWord.setCodeExt("");
        w.setCode(w.getCode() + w.getCodeExt());
        gv.increaseCodeLengthCounter(code.length())
          .updateCodeSetCounter(code);
        if (gv.isInGB2312Set(w.getWord())) {
            gv.addToResult(newWord);
        } else {
            gv.addToUncommon(newWord);
        }
        if (code.length() < 4) {
            gv.addToFull(w);
        }
    }


}
