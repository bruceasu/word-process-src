import java.io.*;
import java.util.HashSet;
import java.util.Set;

public class CharsetClassifier {

    // 用于存储各个字符集合
    private static Set<String> gb2312_1 = loadSet("res/gb2312_1.txt");
    private static Set<String> japaneseCommon = loadSet("res/japanese_common.txt");
    private static Set<String> big5_1 = loadSet("res/big5_1.txt");
    private static Set<String> hk = loadSet("res/hk.txt");
    private static Set<String> standard = loadSet("res/8000.txt");

    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Usage: java CharacterClassifier <input-file>");
            System.exit(1);
        }

        String inputFile = args[0];

        // 定义输出文件（按分类保存）
        try (
                BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(inputFile), "UTF-8"));
                PrintWriter outLevel1 = new PrintWriter(new OutputStreamWriter(new FileOutputStream("level1.txt"), "UTF-8"));
                PrintWriter outLevel2 = new PrintWriter(new OutputStreamWriter(new FileOutputStream("level2.txt"), "UTF-8"));
                PrintWriter outLevel3 = new PrintWriter(new OutputStreamWriter(new FileOutputStream("level3.txt"), "UTF-8"));
                PrintWriter outLevel4 = new PrintWriter(new OutputStreamWriter(new FileOutputStream("level4.txt"), "UTF-8"));
                PrintWriter outOther = new PrintWriter(new OutputStreamWriter(new FileOutputStream("other.txt"), "UTF-8"))
        ) {
            String line;
            while ((line = reader.readLine()) != null) {
                // 跳过空行
                if (line.trim().isEmpty()) {
                    continue;
                }

                // 按制表符分隔（假定正好有两部分）
                String[] parts = line.split("\t");
                if (parts.length < 2) {
                    // 如果格式不对，可以选择跳过或者记录错误日志
                    continue;
                }
                String key = parts[0].trim();
                if (key.isEmpty()) {
                    continue;
                }


                // 分类判断：顺序依次为1级、2级、3级、4级，其余归为非常用字
                if (isGb2312_1(key)) {
                    outLevel1.println(line);
                } else if (isJapaneseCommon(key)) {
                    outLevel2.println(line);
                } else if (isBig5_1(key) || isHk(key)) {
                    outLevel3.println(line);

                } else if (isStandard(key)) {
                    outLevel4.println(line);
                } else {
                    outOther.println(line);
                }
            }
            System.out.println("处理完成。");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 判断字符是否属于 gb2312_1 集合
     */
    private static boolean isGb2312_1(String s) {
        return gb2312_1.contains(s);
    }

    /**
     * 判断字符是否属于 日本当用漢字 集合
     */
    private static boolean isJapaneseCommon(String s) {
        return japaneseCommon.contains(s);
    }

    /**
     * 判断字符是否属于 big5_1 集合
     */
    private static boolean isBig5_1(String s) {
        return big5_1.contains(s);
    }

    private static boolean isHk(String s) {
        return hk.contains(s);
    }

    /**
     * 规范字（standard）集合
     */
    private static boolean isStandard(String s) {
        return standard.contains(s);
    }

    /**
     * 从指定的文件中加载字符集合。
     * 每个文件按行存放一个字符，要求文件编码为 UTF-8。
     */
    private static Set<String> loadSet(String fileName) {
        Set<String> set = new HashSet<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), "UTF-8"))) {
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