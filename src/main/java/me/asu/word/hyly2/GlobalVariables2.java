package me.asu.word.hyly2;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import me.asu.util.Strings;
import me.asu.word.ResourcesFiles;
import me.asu.word.Word;

@Getter
@Slf4j
public class GlobalVariables2 {

    // group
    List<Word> groupTraditionCommon  = new ArrayList<>();
    List<Word> groupTradition  = new ArrayList<>();
    List<Word> groupSimplification   = new ArrayList<>();
    List<Word> groupJapanese  = new ArrayList<>();
    List<Word> groupOther = new ArrayList<>();

    Set<String> w500    = new HashSet<>(ResourcesFiles.readLinesInResources("common-t-words-500.txt"));
    Set<String> w1000   = new HashSet<>(ResourcesFiles.readLinesInResources("common-t-words-1000.txt"));
    Set<String> w2000   = new HashSet<>(ResourcesFiles.readLinesInResources("common-t-words-2000.txt"));
    Set<String> wGb2312 = ResourcesFiles.gb2312();
    Set<String> wBig5_common = ResourcesFiles.big5_common();
    Set<String> wBig5 = ResourcesFiles.big5();
    Set<String> wBig5_hkscs = ResourcesFiles.big5_hkscs();
    Set<String> wJapanese = ResourcesFiles.japanese();
    Set<String> wGeneralSpecification = ResourcesFiles.generalSpecification();

    List<String> single  = new ArrayList<>();
    // 唯一常用字（4000）
    List<Word>   result  = new ArrayList<>();
    // 重码常用字（4000）
    List<Word>   result2 = new ArrayList<>();

    // 唯一通用字（gb2312group）
    List<Word> result3 = new ArrayList<>();
    // 重码通用字（gb2312group）
    List<Word> result4 = new ArrayList<>();
    // 唯一罕用字（非gb2312group）
    List<Word> result5 = new ArrayList<>();
    // 重码罕用字（非gb2312group）
    List<Word> result6 = new ArrayList<>();
    // 占位简码
    List<Word> result7 = new ArrayList<>();
    List<Word> full    = new ArrayList<>();

    List<Word> remain = new ArrayList<>();

    Map<String, AtomicInteger> codeSet  = new HashMap<>();
    Map<String, AtomicInteger> code3Set = new HashMap<>();

    int[] codeLenCounter = new int[30];

    public GlobalVariables2() {
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

    public GlobalVariables2 increaseCode3SetCounter(String code) {
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

    public GlobalVariables2 increaseCodeLengthCounter(int idx) {
        codeLenCounter[idx] += 1;
        return this;
    }

    public GlobalVariables2 addToResult(Word w) {
        result.add(w);
        return this;
    }

    public GlobalVariables2 addToResult2(Word w) {
        result2.add(w);
        return this;
    }

    public GlobalVariables2 addToResult3(Word w) {
        result3.add(w);
        return this;
    }

    public GlobalVariables2 addToResult4(Word w) {
        result4.add(w);
        return this;
    }

    public GlobalVariables2 addToResult5(Word w) {
        result5.add(w);
        return this;
    }

    public GlobalVariables2 addToResult6(Word w) {
        result6.add(w);
        return this;
    }

    public GlobalVariables2 addToResult7(Word w) {
        result7.add(w);
        return this;
    }

    public GlobalVariables2 addToFull(Word w) {
        full.add(w);
        return this;
    }


    public GlobalVariables2 addToSingle(String w) {
        single.add(w);
        return this;
    }

    public GlobalVariables2 addToGroupOther(Word w) {
        groupOther.add(w);
        return this;
    }

    public GlobalVariables2 addToSimplification(Word w) {
        groupSimplification.add(w);
        return this;
    }

    public GlobalVariables2 addToTraditionCommon(Word w) {
        groupTraditionCommon.add(w);
        return this;
    }

    public GlobalVariables2 addToTradition(Word w) {
        groupTradition.add(w);
        return this;
    }

    public GlobalVariables2 addToJapanese(Word w) {
        groupJapanese.add(w);
        return this;
    }

    public GlobalVariables2 addToRemain(Word w) {
        remain.add(w);
        return this;
    }

    public List<Word> getRemain() {
        return remain;
    }

    public GlobalVariables2 updateCodeSetCounter(String code) {
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

    public GlobalVariables2 addCodeSetCounter(String code) {
        codeSet.put(code, new AtomicInteger(1));
        return this;
    }

    public int getCostSetCount(String code) {
        AtomicInteger c = codeSet.get(code);
        if (c == null) { return 0; }
        return c.get();
    }

    public GlobalVariables2 increaseCodeSetCounter(String code) {
        codeSet.get(code).incrementAndGet();
        return this;
    }

    public boolean isInCodeSet(String code) {
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