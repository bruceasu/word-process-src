package me.asu.phrase;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import me.asu.word.ResourcesFiles;

/**
 * @author suk
 */
public class CleanupPhrases {

    public static void main(String[] args) throws IOException {
        Map<String, List<String>> words = ResourcesFiles.loadAsMapList("single.txt");

        File out1 = new File("out", "c.txt");
        File out2 = new File("out", "d.txt");

        List<String> nouns = ResourcesFiles.readLinesInResources("名词.txt");
        Set<String> nounsSet = new HashSet<>();
        nounsSet.addAll(nouns);
        Map<String, String> phrases = ResourcesFiles
                .loadAsMap("out\\he-phrases.txt");
        List<String> needCode = new ArrayList<>();
        try (FileWriter fw1 = new FileWriter(out1);
             FileWriter fw2 = new FileWriter(out2)) {
            for (String n : nouns) {
                if (n.length() == 1) {
                    System.out.println("Single phrase: " + n);
                    continue;
                } else if (n.length() == 2) {
                    needCode.add(n);
                } else if (n.length() == 3) {
                    if (phrases.containsKey(n)) {
                        fw2.write(n + "\t" + phrases.get(n).substring(0, 3));
                        fw2.write("\n");
                    } else {
                        needCode.add(n);
                    }
                } else {
                    if (phrases.containsKey(n)) {
                        fw2.write(n + "\t" + phrases.get(n));
                        fw2.write("\n");
                    } else {
                        needCode.add(n);
                    }
                }
            }
            for (String s : needCode) {
                int length = s.length();
                if (length <= 1) continue;
                List<String> one, two, three, four;
                switch(length) {
                    case 1: continue;
                    case 2:
                        one = words.get(s.substring(0,1));
                        two = words.get(s.substring(1,2));
                        if (one == null || one.isEmpty()
                                || two == null || two.isEmpty()) {
                            System.out.println(s);
//                            fw1.write(s);
//                            fw1.write("\n");
                        } else {
                            for (String s1 : one) {
                                for (String s2 : two) {
                                    fw1.write(String.format("%s\t%s%s\n",
                                            s, s1.substring(0,2), s2.substring(0,2)));
                                }
                            }
                        }
                        break;
                    case 3:
                        one = words.get(s.substring(0,1));
                        two = words.get(s.substring(1,2));
                        three = words.get(s.substring(2,3));
                        if (one == null || one.isEmpty()
                                || two == null || two.isEmpty()
                                || three == null || three.isEmpty()) {
                            System.out.println(s);
//                            fw1.write(s);
//                            fw1.write("\n");
                        } else {
                            for (String s1 : one) {
                                for (String s2 : two) {
                                    for (String s3 : three) {
                                        fw1.write(String.format("%s\t%s%s%s\n", s,
                                                s1.charAt(0), s2.charAt(0), s3.charAt(0)));
                                    }
                                }
                            }
                        }
                        break;
                    default:
                        one = words.get(s.substring(0,1));
                        two = words.get(s.substring(1,2));
                        three = words.get(s.substring(2,3));
                        four = words.get(s.substring(length-1,length));
                        if (one == null || one.isEmpty()
                                || two == null || two.isEmpty()
                                || three == null || three.isEmpty()
                                || four == null || four.isEmpty()) {
                            System.out.println(s);
//                            fw1.write(s);
//                            fw1.write("\n");
                        } else {
                            for (String s1 : one) {
                                for (String s2 : two) {
                                    for (String s3 : three) {
                                        for (String s4 : four) {
                                            fw1.write(String.format(
                                                    "%s\t%s%s%s%s\n", s,
                                                    s1.charAt(0), s2.charAt(0), s3.charAt(0), s4.charAt(0)));
                                        }
                                    }
                                }
                            }
                        }
                        break;
                }


            }
        }

        System.out.println("Output to: " + out1);
        System.out.println("Output to: " + out2);
        System.out.println("Done!");
    }

}
