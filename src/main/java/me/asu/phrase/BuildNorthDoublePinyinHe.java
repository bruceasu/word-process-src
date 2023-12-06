package me.asu.phrase;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class BuildNorthDoublePinyinHe {

    public static void main(String[] args) throws IOException {
//        String inFile = "D:\\suk\\Desktop\\2.txt";
        String inFile = "out\\a.txt";
        String outFile = "out\\b.txt";
        String fileCharset = "UTF-8";

        makeDoublePinYinHe(inFile, outFile, fileCharset);
        System.out.println("Done.");
    }

    static void makeDoublePinYinHe(String inFile, String outFile,
                                   String fileCharset) throws IOException {
        Charset charset = Charset.forName(fileCharset);
        Map<String, String> mapping = makeTable();
        int cnt = 0;
        try (BufferedReader reader = Files
                .newBufferedReader(Paths.get(inFile), charset);
             BufferedWriter writer = Files
                     .newBufferedWriter(Paths.get(outFile), charset)) {
            StringBuilder builder = new StringBuilder();
            do {
                String line = reader.readLine();
                cnt++;
                if (line == null) {
                    break;
                }
                line = line.trim();
                if (line.startsWith("#") || line.isEmpty()) {
                    continue;
                }

                String[] data = line.split("\\s+");
                if (data.length < 2) {
                    continue;
                }
                builder.setLength(0);
                final String phrase = data[0];
                builder.append(phrase);
                builder.append('\t');
                boolean error = false;
                for (int i = 1; i < data.length; i++) {
                    String code = data[i];
                    if (i > 1) {
                        builder.append(' ');
                    }
                    boolean hasTone = code.matches(".+\\d$");
                    if (hasTone) {
                        String newCode = code.substring(0, code.length()-1);
                        String tone = code.substring(code.length()-1);
                        if (newCode.length() == 1) {
                            builder.append(code).append(code);
                        } else if (newCode.length() == 2) {
                            builder.append(code);
                        } else {
                            if (mapping.containsKey(newCode)) {
                                builder.append(mapping.get(newCode) + tone);
                            } else {
                                System.out.printf("line %s key not in mapping %s%n",
                                        cnt, code);
                                error = true;
                                break;
                            }
                        }
                    } else {
                        if (code.length() == 1) {
                            builder.append(code).append(code);
                        } else if (code.length() == 2) {
                            builder.append(code);
                        } else {
                            if (mapping.containsKey(code)) {
                                builder.append(mapping.get(code));
                            } else {
                                System.out.printf("line %s key not in mapping %s%n",
                                        cnt, code);
                                error = true;
                                break;
                            }
                        }
                    }


                }
                if (error) {
                    System.out.printf("line %s:%s has not map code.%n",
                            cnt, line);
                } else {
                    builder.append('\n');
                    writer.write(builder.toString());
                }

                if (cnt % 10000 == 0) {
                    System.out.printf("Processed %d lines.%n", cnt);
                }
            } while (true);

        }
        System.out.printf("Processed %d lines.%n", cnt);
    }

    static Map<String, String> makeTable() {
        Map<String, String> s = new HashMap<>();
        Map<String, String> w = new HashMap<>();
        Map<String, String> m = new HashMap<>();
        s.put("q", "q");
        s.put("w", "w");
        s.put("r", "r");
        s.put("t", "t");
        s.put("y", "y");
        s.put("sh", "u");
        s.put("ch", "i");
        s.put("p", "p");
        s.put("s", "s");
        s.put("d", "d");
        s.put("f", "f");
        s.put("g", "g");
        s.put("h", "h");
        s.put("j", "j");
        s.put("k", "k");
        s.put("l", "l");
        s.put("z", "z");
        s.put("x", "x");
        s.put("c", "c");
        s.put("zh", "v");
        s.put("b", "b");
        s.put("n", "n");
        s.put("m", "m");

        w.put("iu", "q");
        w.put("ei", "w");
        w.put("e", "e");
        w.put("uan", "r");
        w.put("van", "r");
        w.put("ve", "t");
        w.put("ue", "t");
        w.put("üe", "t");
        w.put("un", "y");
        w.put("vn", "y");
        w.put("u", "u");
        w.put("i", "i");
        w.put("o", "o");
        w.put("uo", "o");
        w.put("ie", "p");

        w.put("a", "a");
        w.put("ong", "s");
        w.put("iong", "s");
        w.put("ai", "d");
        w.put("en", "f");
        w.put("eng", "g");
        w.put("ang", "h");
        w.put("an", "j");
        w.put("ing", "k");
        w.put("uai", "k");
        w.put("uang", "l");
        w.put("iang", "l");

        w.put("ou", "z");
        w.put("ua", "x");
        w.put("ia", "x");
        w.put("ao", "c");
        w.put("v", "v");
        w.put("ui", "v");
        w.put("in", "b");
        w.put("iao", "n");
        w.put("ian", "m");

        s.forEach((k1, v1) -> {
            w.forEach((k2, v2) -> {
                m.put(k1 + k2, v1 + v2);
            });
        });
        m.put("ang", "ag");
        m.put("eng", "eg");
        m.put("a", "aa");
        m.put("e", "ee");
        m.put("o", "oo");

        return m;
    }


}


