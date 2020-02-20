package me.asu.word.shortern;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import lombok.Data;
import me.asu.word.Word;

@Data
public class Options {

    GlobalVariables                      gv;
    Map<Integer, Function<Word, String>> functions  = new HashMap<>();
    Map<Integer, Predicate<Word>>        predicates = new HashMap<>();
    Map<Integer, Integer>                offsets    = new HashMap<>();
    List<Word>                               group;

    public Options globalVariables(GlobalVariables gv)
    {
        this.gv = gv;
        return this;
    }
    public Options group(List<Word> g)
    {
        this.group = g;
        return this;
    }

    public Options function(Integer len, Function<Word, String> func)
    {
        functions.put(len, func);
        return this;
    }

    public Function<Word, String> function(int len)
    {
        return functions.get(len);
    }

    public Options predicate(int len, Predicate<Word> predicate)
    {
        predicates.put(len, predicate);
        return this;
    }

    public Predicate<Word> predicate(int len)
    {
        return predicates.get(len);
    }

    public Options offset(int len, int offset)
    {
        offsets.put(len, offset);
        return this;
    }

    public int offset(int len)
    {
        Integer integer = offsets.get(len);
        if (integer == null) {
            return 0;
        }
        return integer;
    }
}
