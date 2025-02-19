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
public class GlobalVariablesTraditional {

    // group
    public List<Word> groupTraditionCommon  = new ArrayList<>();
    public List<Word> groupTradition  = new ArrayList<>();
    public List<Word> groupSimplification   = new ArrayList<>();
    public List<Word> groupJapanese  = new ArrayList<>();
    public List<Word> groupOther = new ArrayList<>();

    public Set<String> w500    = new HashSet<>(ResourcesFiles.readLinesInResources("common-t-words-500.txt"));
    public Set<String> w1000   = new HashSet<>(ResourcesFiles.readLinesInResources("common-t-words-1000.txt"));
    public Set<String> w2000   = new HashSet<>(ResourcesFiles.readLinesInResources("common-t-words-2000.txt"));
    public Set<String> wGb2312 = ResourcesFiles.gb2312();
    public Set<String> wBig5_common = ResourcesFiles.big5_common();
    public Set<String> wBig5 = ResourcesFiles.big5();
    public Set<String> wBig5_hkscs = ResourcesFiles.big5_hkscs();
    public Set<String> wJapanese = ResourcesFiles.japanese();
    public Set<String> wGeneralSpecification = ResourcesFiles.generalSpecification();

    public List<String> single  = new ArrayList<>();
    // 唯一常用字（4000）
    public List<Word>   result  = new ArrayList<>();
    // 重码常用字（4000）
    public List<Word>   result2 = new ArrayList<>();

    // 唯一通用字（gb2312group）
    public List<Word> result3 = new ArrayList<>();
    // 重码通用字（gb2312group）
    public List<Word> result4 = new ArrayList<>();
    // 唯一罕用字（非gb2312group）
    public List<Word> result5 = new ArrayList<>();
    // 重码罕用字（非gb2312group）
    public List<Word> result6 = new ArrayList<>();
    // 占位简码
    public List<Word> result7 = new ArrayList<>();
    public List<Word> full    = new ArrayList<>();

    public  List<Word> remain = new ArrayList<>();

    Map<String, AtomicInteger> codeSet  = new HashMap<>();
    Map<String, AtomicInteger> code3Set = new HashMap<>();

    int[] codeLenCounter = new int[30];

    public GlobalVariablesTraditional() {
        for (int i = 0; i < 30; i++) {
            codeLenCounter[i] = 0;
        }
    }

    public void printCounter() {
        StringBuilder builder = new StringBuilder();
        for (int i = 1; i < codeLenCounter.length; i++) {
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
            if (Strings.isNotBlank(w.getCodeExt())) {
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
    public boolean isInBig5Common(String w) {
        return wBig5_common.contains(w);
    }
    public boolean isInBig5Hkscs(String w) {
        return wBig5_hkscs.contains(w);
    }
    public boolean isInBig5(String w) {
        return wBig5.contains(w);
    }
    public boolean isInGeneralSpecification(String w) {
        return wGeneralSpecification.contains(w);
    }

    public boolean isInJapanese(String w) {
        return wJapanese.contains(w);
    }
    public boolean isIn2000Set(String w) {
        return w2000.contains(w);
    }

    public boolean isIn1000Set(String w) {
        return w1000.contains(w);
    }

    public boolean isIn500Set(String w) {
        return w500.contains(w);
    }

    public boolean isInSingleSet(String w) {
        return single.contains(w);
    }

    public GlobalVariablesTraditional increaseCode3SetCounter(String code) {
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

    public GlobalVariablesTraditional increaseCodeLengthCounter(int idx) {
        codeLenCounter[idx] += 1;
        return this;
    }

    public GlobalVariablesTraditional addToResult(Word w) {
        result.add(w);
        return this;
    }

    public GlobalVariablesTraditional addToResult2(Word w) {
        result2.add(w);
        return this;
    }

    public GlobalVariablesTraditional addToResult3(Word w) {
        result3.add(w);
        return this;
    }

    public GlobalVariablesTraditional addToResult4(Word w) {
        result4.add(w);
        return this;
    }

    public GlobalVariablesTraditional addToResult5(Word w) {
        result5.add(w);
        return this;
    }

    public GlobalVariablesTraditional addToResult6(Word w) {
        result6.add(w);
        return this;
    }

    public GlobalVariablesTraditional addToResult7(Word w) {
        result7.add(w);
        return this;
    }

    public GlobalVariablesTraditional addToFull(Word w) {
        full.add(w);
        return this;
    }


    public GlobalVariablesTraditional addToSingle(String w) {
        single.add(w);
        return this;
    }

    public GlobalVariablesTraditional addToGroupOther(Word w) {
        groupOther.add(w);
        return this;
    }

    public GlobalVariablesTraditional addToSimplification(Word w) {
        groupSimplification.add(w);
        return this;
    }

    public GlobalVariablesTraditional addToTraditionCommon(Word w) {
        groupTraditionCommon.add(w);
        return this;
    }

    public GlobalVariablesTraditional addToTradition(Word w) {
        groupTradition.add(w);
        return this;
    }

    public GlobalVariablesTraditional addToJapanese(Word w) {
        groupJapanese.add(w);
        return this;
    }

    public GlobalVariablesTraditional addToRemain(Word w) {
        remain.add(w);
        return this;
    }

    public List<Word> getRemain() {
        return remain;
    }

    public GlobalVariablesTraditional updateCodeSetCounter(String code) {
        if (isNotInCodeSet(code)) {
            addCodeSetCounter(code);
        } else {
            increaseCodeSetCounter(code);
        }

        return this;
    }

    public boolean isNotInCodeSet(String code) {
        return !isInCodeSet(code);
    }

    public GlobalVariablesTraditional addCodeSetCounter(String code) {
        codeSet.put(code, new AtomicInteger(1));
        return this;
    }

    public int getCodeSetCount(String code) {
        AtomicInteger c = codeSet.get(code);
        if (c == null) { return 0; }
        return c.get();
    }

    public GlobalVariablesTraditional increaseCodeSetCounter(String code) {
        codeSet.get(code).incrementAndGet();
        return this;
    }

    public boolean isInCodeSet(String code) {
        return codeSet.containsKey(code);
    }
}