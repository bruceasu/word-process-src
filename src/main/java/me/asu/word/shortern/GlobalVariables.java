package me.asu.word.shortern;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import me.asu.util.Strings;
import me.asu.word.ResourcesFiles;
import me.asu.word.Word;

@Getter
@Slf4j
public class GlobalVariables {

    // group
    List<Word> group500   = new ArrayList<>();
    List<Word> group1600  = new ArrayList<>();
    List<Word> group3800  = new ArrayList<>();
    List<Word> group4200  = new ArrayList<>();
    List<Word> groupOther = new ArrayList<>();

    Set<String> w500    = ResourcesFiles.w500();
    Set<String> w1600   = ResourcesFiles.w1600();
    Set<String> w3800   = ResourcesFiles.w3800();
    Set<String> w4200   = ResourcesFiles.w4200();
    Set<String> wGb2312 = ResourcesFiles.gb2312();

    List<String> single   = new ArrayList<>();
    List<Word>   result   = new ArrayList<>();
    List<Word>   result2  = new ArrayList<>();
    List<Word>   full     = new ArrayList<>();
    List<Word>   uncommon = new ArrayList<>();
    List<Word>   remain = new ArrayList<>();

    Map<String, AtomicInteger> codeSet = new HashMap<>();
    Map<String, AtomicInteger> code3Set = new HashMap<>();

    int[] codeLenCounter = new int[30];

    public GlobalVariables() {
        for (int i = 0; i < 30; i++) {
            codeLenCounter[i] = 0;
        }
    }

    public void printCounter() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < codeLenCounter.length; i++) {
            builder.append(codeLenCounter[i]).append(", ");
        }
        if (builder.length() > 0) {
            builder.setLength(builder.length() - 2);
        }
        log.info(builder.toString());
    }

    public Map<String, List<Word>> groupSyllablesForOther() {
       return groupSyllablesForOther("");
    }

    public Map<String, List<Word>> groupSyllablesForOther(String delimiter) {
        Map<String, List<Word>> groupBySyllables = new TreeMap<>();
        Collections.sort(groupOther);
        log.info("groupOther: {}", groupOther.size());
        for (Word w : groupOther) {
            String code = w.getCode() + delimiter;
            if(Strings.isNotBlank(w.getCodeExt())) {
                code += w.getCodeExt();
            }
            if (!groupBySyllables.containsKey(code)) {
                groupBySyllables.put(code, new ArrayList<>());
            }
            groupBySyllables.get(code).add(w);
        }
        log.info("groupBySyllables: {}", groupBySyllables.size());
        return groupBySyllables;
    }

    public boolean isInGB2312Set(String w) {
        return wGb2312.contains(w);
    }
    public boolean isIn4200Set(String w) {
        return w4200.contains(w);
    }

    public boolean isIn3800Set(String w) {
        return w3800.contains(w);
    }
    public boolean isIn1600Set(String w) {
        return w1600.contains(w);
    }
    public boolean isIn500Set(String w) {
        return w500.contains(w);
    }
    public boolean isInSingleSet(String w) {
        return single.contains(w);
    }

    public GlobalVariables increaseCode3SetCounter(String code) {
        AtomicInteger atomicInteger = code3Set.get(code);
        if (atomicInteger == null) {
            atomicInteger = new AtomicInteger();
            code3Set.put(code, atomicInteger);
        }
        atomicInteger.incrementAndGet();
        return this;
    }

    public boolean isCode3setGt(String code, int n) {
        AtomicInteger atomicInteger = code3Set.get(code);
        if (atomicInteger == null) {
            return false;
        }
        return atomicInteger.get() > n;
    }

    public GlobalVariables increaseCodeLengthCounter(int idx) {
        codeLenCounter[idx] += 1;
        return this;
    }

    public GlobalVariables addToResult(Word w) {
        result.add(w);
        return this;
    }

    public GlobalVariables addToResult2(Word w)
    {
        result2.add(w);
        return this;
    }
    public GlobalVariables addToFull(Word w) {
        full.add(w);
        return this;
    }

    public GlobalVariables addToUncommon(Word w) {
        uncommon.add(w);
        return this;
    }

    public GlobalVariables addToSingle(String w) {
        single.add(w);
        return this;
    }

    public GlobalVariables addToGroupOther(Word w) {
        groupOther.add(w);
        return this;
    }

    public GlobalVariables addToG500(Word w) {
        group500.add(w);
        return this;
    }

    public GlobalVariables addToG1600(Word w) {
        group1600.add(w);
        return this;
    }

    public GlobalVariables addToG3800(Word w) {
        group3800.add(w);
        return this;
    }

    public GlobalVariables addToG4200(Word w) {
        group4200.add(w);
        return this;
    }

    public GlobalVariables addToRemain(Word w) {
        remain.add(w);
        return this;
    }

    public List<Word> getRemain() {
        return remain;
    }

    public GlobalVariables updateCodeSetCounter(String code) {
        if (isNotInCodeSet(code)) {
            addCodeSetCounter(code);
        } else {
            increaseCodeSetCounter(code);
        }

        return this;
    }

    public boolean isNotInCodeSet(String code)
    {
        return !isInCodeSet(code);
    }

    public GlobalVariables addCodeSetCounter(String code)
    {
        codeSet.put(code, new AtomicInteger(1));
        return this;
    }

    public GlobalVariables increaseCodeSetCounter(String code)
    {
        codeSet.get(code).incrementAndGet();
        return this;
    }

    public boolean isInCodeSet(String code)
    {
        return codeSet.containsKey(code);
    }

    public int getCodeSetCount(String code) {
        if (codeSet.containsKey(code)) {
            return codeSet.get(code).get();
        } else {
            return 0;
        }
    }
}