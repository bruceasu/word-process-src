package me.asu.word;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import lombok.Data;

/**
 * Created by suk on 2019/6/2.
 */
@Data
public class Word implements Comparable<Word>
{
    String word;
    String code;
    String codeExt;
    int level;
    int order;
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
}
