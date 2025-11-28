package res;

import java.io.*;
import java.util.HashSet;
import java.util.Set;

public class TagClassifier {

    // 用于存储各个字符集合
    private static Set<String> gb2312_1 = loadSet("gb2312_1.txt"); // 1级字
    private static Set<String> gb2312 = loadSet("gb2312-utf8.txt"); // 1+2级字
    private static Set<String> standard = loadSet("standard.txt"); // 规范字
    private static Set<String> japaneseCommon = loadSet("japanese_common.txt"); // 日本常用字
    private static Set<String> big5_1 = loadSet("big5_1.txt"); // 繁体1级字
    private static Set<String> big5 = loadSet("big5.txt"); // 繁体1+2级字
    private static Set<String> hk = loadSet("hk.txt"); // 香港字

    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Usage: java TagClassifier <input-file>");
            System.exit(1);
        }

        String inputFile = args[0];

        // 定义输出文件（按分类保存）
        try (BufferedReader reader =
                     new BufferedReader(new InputStreamReader(new FileInputStream(inputFile), "UTF-8"));
             PrintWriter out = new PrintWriter(
                     new OutputStreamWriter(new FileOutputStream("out.txt"), "UTF-8"));) {
            String line;
            while ((line = reader.readLine()) != null) {
                // 跳过空行
                if (line.trim().isEmpty() || line.trim().charAt(0) == '#') {
                    out.println(line);
                    continue;
                }

                // 按制表符分隔（假定正好有两部分）
                String[] parts = line.split("\t");

                if (parts.length < 2)
                    continue;

                String key = parts[0].trim();
                if (isSet(japaneseCommon, key)) {
                    if (isSet(gb2312_1, key)) {
                        out.printf("1 %s\t%s%n", key, "1级・日");
                    } else if (isSet(gb2312, key)) {
                        out.printf("2 %s\t%s%n", key, "2级・日");
                    } else if (isSet(standard, key)) {
                        out.printf("3 %s\t%s%n", key, "规范・日");
                    } else if (isSet(big5_1, key)) {
                        out.printf("5 %s\t%s%n", key, "3级・日");
                    } else if (isSet(big5, key)) {
                        out.printf("6 %s\t%s%n", key, "4级・日");
                    } else if (isSet(japaneseCommon, key)) {
                        out.printf("4 %s\t%s%n", key, "日字");
                    } else if (isSet(hk, key)) {
                        out.printf("7 %s\t%s%n", key, "香港・日");
                    } else {
                        out.println("9\t" + line);
                    }
                } else {
                    if (isSet(gb2312_1, key)) {
                        out.printf("1 %s\t%s%n", key, "1级");
                    } else if (isSet(gb2312, key)) {
                        out.printf("2 %s\t%s%n", key, "2级");
                    } else if (isSet(standard, key)) {
                        out.printf("3 %s\t%s%n", key, "规范");
                    } else if (isSet(big5_1, key)) {
                        out.printf("5 %s\t%s%n", key, "3级");
                    } else if (isSet(big5, key)) {
                        out.printf("6 %s\t%s%n", key, "4级");
                    } else if (isSet(japaneseCommon, key)) {
                        out.printf("4 %s\t%s%n", key, "日字");
                    } else if (isSet(hk, key)) {
                        out.printf("7 %s\t%s%n", key, "香港");
                    } else {
                        out.println("9 " + line);
                    }
                }

            }
            System.out.println("Write to out.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static boolean isSet(Set<String> set, String s) {
        return set.contains(s);
    }


    /**
     * 从指定的文件中加载字符集合。 每个文件按行存放一个字符，要求文件编码为 UTF-8。
     */
    private static Set<String> loadSet(String fileName) {
        Set<String> set = new HashSet<>();
        try (BufferedReader br =
                     new BufferedReader(new InputStreamReader(new FileInputStream(fileName), "UTF-8"))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) {
                    set.add(line);
                }
            }
        } catch (IOException e) {
            System.err.println("加载文件 " + fileName + " 出错：" + e.getMessage());
        }
        return set;
    }
}