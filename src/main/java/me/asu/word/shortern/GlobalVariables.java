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
    List<Word> group1    = new ArrayList<>();
    List<Word> group2    = new ArrayList<>();
    List<Word> group3    = new ArrayList<>();
    List<Word> group4     = new ArrayList<>();
    List<Word> group5     = new ArrayList<>();
    List<Word> group6     = new ArrayList<>();
    List<Word> group7     = new ArrayList<>();
    List<Word> group8     = new ArrayList<>();
    List<Word> group9     = new ArrayList<>();
    List<Word> groupOther = new ArrayList<>();

    Set<String> w500    = Collections.emptySet(); //ResourcesFiles.w500();
    Set<String> w1000   = Collections.emptySet(); //ResourcesFiles.w1000();
    Set<String> w2000   = Collections.emptySet(); //ResourcesFiles.w2000();
    Set<String> wCj2000   = Collections.emptySet(); //ResourcesFiles.w2000();
    Set<String> wC2000   = Collections.emptySet(); //ResourcesFiles.w2000();
    Set<String> w4000   = Collections.emptySet(); //ResourcesFiles.w4000();
    Set<String> wGb = Collections.emptySet(); //ResourcesFiles.gb2312();
    Set<String> wGb2312 = Collections.emptySet(); //ResourcesFiles.gb2312();
    Set<String> wGb2312_1 = Collections.emptySet(); //ResourcesFiles.gb2312();

    Set<String> wBig5_common = Collections.emptySet(); //ResourcesFiles.big5_common();
    Set<String> wBig5        = Collections.emptySet(); //ResourcesFiles.big5();
    Set<String> wBig5Hkscs   = Collections.emptySet(); //ResourcesFiles.big5_hkscs();

    Set<String> wJp       = Collections.emptySet(); //ResourcesFiles.japanese();
    Set<String> wJpCommon = Collections.emptySet(); //ResourcesFiles.japaneseCommon();
    Set<String> wJpLevel1 = Collections.emptySet(); //ResourcesFiles.japaneseLevel1();
    Set<String> wJpLevel2 = Collections.emptySet(); //ResourcesFiles.japaneseLevel2();
    Set<String> wJpLevel3 = Collections.emptySet(); //ResourcesFiles.japaneseLevel2();
    Set<String> wJpLevel4 = Collections.emptySet(); //ResourcesFiles.japaneseLevel2();
    Map<String, Integer> jpOrder = Collections.emptyMap();

    Set<String> wGeneralSpecification = Collections.emptySet(); //ResourcesFiles.generalSpecification();

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
    List<Word> result8 = new ArrayList<>();
    List<Word> result9 = new ArrayList<>();
    List<Word> full    = new ArrayList<>();

    List<Word> remain = new ArrayList<>();

    Map<String, AtomicInteger> codeSet  = new HashMap<>();
    Map<String, AtomicInteger> code3Set = new HashMap<>();

    int[] codeLenCounter = new int[30];

    public GlobalVariables() {
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

    public Integer getJpOrder(String w) {
        if(isMapEmpty(jpOrder)) {
            jpOrder=new HashMap<>();
            Map<String, String> m = ResourcesFiles.loadCsvAsMap("jp-words-order.csv");
            m.forEach((k,v)->{
                jpOrder.put(v, -1 * Integer.valueOf(k));
            });
        }
        Integer freq = jpOrder.get(w);
        return freq == null ? Integer.MAX_VALUE : freq;
    }

    public boolean isInBig5Common(String w) {
        if(isCollectionEmpty(wBig5_common)) {
            wBig5_common = ResourcesFiles.big5_common();
        }
        return wBig5_common.contains(w);
    }
    public boolean isInCj2000(String w) {
        if(isCollectionEmpty(wCj2000)) {
            wCj2000 = ResourcesFiles.wCj2000();
        }
        return wCj2000.contains(w);
    }
    public boolean isInC2000(String w) {
        if(isCollectionEmpty(wC2000)) {
            wC2000 = ResourcesFiles.wC2000();
        }
        return wC2000.contains(w);
    }

    public boolean isInBig5Hkscs(String w) {
        if(isCollectionEmpty(wBig5Hkscs)) {
            Set<String> b =  ResourcesFiles.big5();
            wBig5Hkscs =  ResourcesFiles.big5_hkscs();
            wBig5Hkscs.removeAll(b);
        }
        return wBig5Hkscs.contains(w);
    }

    public boolean isInBig5(String w) {
        if (isCollectionEmpty(wBig5)) {
            wBig5 =  ResourcesFiles.big5();
        }
        return wBig5.contains(w);
    }
    public boolean isInGeneralSpecification(String w) {
        if (isCollectionEmpty(wGeneralSpecification)) {
            wGeneralSpecification = ResourcesFiles.generalSpecification();
        }
        return wGeneralSpecification.contains(w);
    }

    public boolean isInJp(String w) {
        if(isCollectionEmpty(wJp)) {
            wJp = ResourcesFiles.japanese();
        }
        return wJp.contains(w);
    }

    public boolean isInJpCommon(String w) {
        if (isCollectionEmpty(wJpCommon)) {
            wJpCommon = ResourcesFiles.japaneseCommon();
        }
        return wJpCommon.contains(w);
    }

    public boolean isInJpLevel1(String w) {
        if (isCollectionEmpty(wJpLevel1)) {
            wJpLevel1 = ResourcesFiles.japaneseLevel1();
        }
        return wJpLevel1.contains(w);
    }

    public boolean isInJpLevel2(String w) {
        if (isCollectionEmpty(wJpLevel2)) {
            wJpLevel2 = ResourcesFiles.japaneseLevel2();
        }
        return wJpLevel2.contains(w);
    }

    public boolean isInJpLevel3(String w) {
        if (isCollectionEmpty(wJpLevel3)) {
            wJpLevel3 = ResourcesFiles.japaneseLevel3();
        }
        return wJpLevel3.contains(w);
    }
    public boolean isInJpLevel4(String w) {
        if (isCollectionEmpty(wJpLevel4)) {
            wJpLevel4 = ResourcesFiles.japaneseLevel4();
        }
        return wJpLevel4.contains(w);
    }
    public boolean isInGB2312(String w) {
        if (isCollectionEmpty(wGb2312)) {
            wGb2312 = ResourcesFiles.gb2312();
        }
        return wGb2312.contains(w);
    }
    public boolean isInGB2312_1(String w) {
        if (isCollectionEmpty(wGb2312_1)) {
            wGb2312_1 = ResourcesFiles.gb2312_1();
        }
        return wGb2312_1.contains(w);
    }
    public boolean isInGb(String w) {
        if (isCollectionEmpty(wGb)) {
            wGb = ResourcesFiles.gb();
        }
        return wGb.contains(w);
    }
    public boolean isIn4000Set(String w) {
        if (isCollectionEmpty(w4000)) {
            w4000 = ResourcesFiles.w4000();
        }
        return w4000.contains(w);
    }

    public boolean isIn2000Set(String w) {
        if (isCollectionEmpty(w2000)) {
            w2000 = ResourcesFiles.w2000();
        }
        return w2000.contains(w);
    }

    public boolean isIn1000Set(String w) {
        if (isCollectionEmpty(w1000)) {
            w1000 = ResourcesFiles.w1000();
        }
        return w1000.contains(w);
    }

    public boolean isIn500Set(String w) {
        if (isCollectionEmpty(w500)) {
            w500 = ResourcesFiles.w500();
        }
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

    public GlobalVariables addToResult2(Word w) {
        result2.add(w);
        return this;
    }

    public GlobalVariables addToResult3(Word w) {
        result3.add(w);
        return this;
    }

    public GlobalVariables addToResult4(Word w) {
        result4.add(w);
        return this;
    }

    public GlobalVariables addToResult5(Word w) {
        result5.add(w);
        return this;
    }

    public GlobalVariables addToResult6(Word w) {
        result6.add(w);
        return this;
    }

    public GlobalVariables addToResult7(Word w) {
        result7.add(w);
        return this;
    }

    public GlobalVariables addToFull(Word w) {
        full.add(w);
        return this;
    }


    public GlobalVariables addToSingle(String w) {
        single.add(w);
        return this;
    }


    public GlobalVariables addToGroup1(Word w) {
        group1.add(w);
        return this;
    }

    public GlobalVariables addToGroup2(Word w) {
        group2.add(w);
        return this;
    }

    public GlobalVariables addToGroup3(Word w) {
        group3.add(w);
        return this;
    }

    public GlobalVariables addToGroup4(Word w) {
        group4.add(w);
        return this;
    }

    public GlobalVariables addToGroup5(Word w) {
        group5.add(w);
        return this;
    }
    public GlobalVariables addToGroup6(Word w) {
        group6.add(w);
        return this;
    }
    public GlobalVariables addToGroup7(Word w) {
        group7.add(w);
        return this;
    }
    public GlobalVariables addToGroup8(Word w) {
        group8.add(w);
        return this;
    }
    public GlobalVariables addToGroup9(Word w) {
        group9.add(w);
        return this;
    }

    public GlobalVariables addToGroupOther(Word w) {
        groupOther.add(w);
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

    public boolean isNotInCodeSet(String code) {
        return !isInCodeSet(code);
    }

    public GlobalVariables addCodeSetCounter(String code) {
        codeSet.put(code, new AtomicInteger(1));
        return this;
    }

    public int getCostSetCount(String code) {
        AtomicInteger c = codeSet.get(code);
        if (c == null) { return 0; }
        return c.get();
    }

    public GlobalVariables increaseCodeSetCounter(String code) {
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

    private boolean isCollectionEmpty(Collection c) {
        return c == null || c.isEmpty();
    }

    private boolean isMapEmpty(Map c) {
        return c == null || c.isEmpty();
    }
}