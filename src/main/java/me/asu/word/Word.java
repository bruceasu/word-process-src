package me.asu.word;

import lombok.Data;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Created by suk on 2019/6/2.
 */
@Data
public class Word implements Comparable<Word>
{
    String word;
    String code;
    String codeExt;
    int level = 100;
    int order = 10000;
    Set<String> tags = new HashSet<>();

    public static int compare(Word o1, Word o2)
    {
        if (o2 == null) {
            return 1;
        }
        int i = o1.getLevel() - o2.getLevel();
        if (i == 0) {
            i = o1.getOrder() - o2.getOrder();
            if (i == 0) {
                i= o1.getCode().compareTo(o2.getCode());

            }
        }
        return i;
    }
    
    @Override
    public Word clone() {
        Word w = new Word();
        w.setWord(this.word);
        w.setCode(this.code);
        w.setLevel(this.level);
        w.setOrder(this.order);
        w.getTags().addAll(this.tags);
        return w;
    }

    @Override
    public int compareTo(Word o) {
        if (o == null) {
            return 1;
        }
        int i = this.getLevel() - o.getLevel();
        if (i == 0) {
            i= this.getCode().compareTo(o.getCode());
            if (i == 0) {
                i = this.getOrder() - o.getOrder();
            }
        }
        return i;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        Word word1 = (Word) o;
        return Objects.equals(word, word1.word) && Objects
                .equals(code, word1.code) && Objects
                .equals(codeExt, word1.codeExt) && Objects
                .equals(tags, word1.tags);
    }

    @Override
    public int hashCode() {
        return Objects.hash(word, code, codeExt, tags);
    }
}