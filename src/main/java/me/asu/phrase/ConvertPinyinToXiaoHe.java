package me.asu.phrase;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 小鹤
 */
public class ConvertPinyinToXiaoHe {

    public static void main(String[] args) throws IOException {
        String inFile = "/run/media/suk/Backup/c.txt";
        String outFile = "/run/media/suk/Backup/k.txt";
        String fileCharset = "UTF-8";

        convertToXiaoHe(inFile, outFile, fileCharset);
        System.out.println("Done.");
    }

    public static void convertToXiaoHe(String inFile, String outFile,
                                         String fileCharset) throws IOException {
        Charset charset = Charset.forName(fileCharset);

        List<Function> rules = makeRules();
        int cnt = 0;
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(inFile), charset);
             BufferedWriter writer = Files.newBufferedWriter(Paths.get(outFile), charset)) {
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

                String[] data = line.split("\t");
                if (data.length < 2) {
                    continue;
                }
                builder.setLength(0);
                final String phrase = data[0];
                builder.append(phrase);
                builder.append('\t');
                String symbols[] = data[1].split(" ");
                for (String symbol : symbols) {
                    for (Function<String, String> rule : rules) {
                        symbol = rule.apply(symbol);
                    }
                    builder.append(symbol).append(' ');
                }

                if (builder.charAt(builder.length()-1) == ' ') {
                    builder.setLength(builder.length()-1);
                }

                builder.append('\n');
                writer.write(builder.toString());

                if (cnt % 10000 == 0) {
                    System.out.printf("Processed %d lines.%n", cnt);
                }
            } while (true);

        }
        System.out.printf("Processed %d lines.%n", cnt);
    }

    static class Xfrom implements Function<String, String> {
        Pattern pattern;
        String replacement;

        Xfrom(String pattern, String replacement) {
            this.pattern = Pattern.compile(pattern);
            this.replacement = replacement;
        }

        // 处理 xform 正则表达式替换
        @Override
        public String apply(String input) {
            Matcher matcher = pattern.matcher(input);
            return matcher.replaceAll(replacement);
        }
    }

    static class Xlit implements Function<String, String> {
        String xlitChars = "ⓆⓌⓇⓉⓎⓊⒾⓄⓅⓈⒹⒻⒼⒽⒿⓀⓁⓏⓍⒸⓋⒷⓃⓂ";
        String normalChars = "qwrtyuiopsdfghjklzxcvbnm";

        Xlit() {}

        Xlit(String xlitChars, String normalChars) {
            this.xlitChars = xlitChars;
            this.normalChars = normalChars;
        }
        // 处理 xlit 映射字符
        @Override
        public String apply(String input) {
            // 创建映射表
            StringBuilder builder = new StringBuilder();
            char[]        chars   = input.toCharArray();
            for (char c : chars) {
                int i = xlitChars.indexOf(c);
                if (i == -1) {
                    builder.append(c);
                } else {
                    builder.append(normalChars.charAt(i));
                }
            }
            return builder.toString();
//            for (int i = 0; i < xlitChars.length(); i++) {
//                input = input.replace(xlitChars.charAt(i), normalChars.charAt(i));
//            }
//
//            return input;
        }
    }

    /**
     * 注意顺序
     * @return
     */
    static List<Function> makeRules() {
        List<Function> result = new ArrayList<>(30);
        /*
           - xform/^([aoe])(ng)?$/$1$1$2/
           - xform/iu$/Ⓠ/
           - xform/(.)ei$/$1Ⓦ/
           - xform/[üu]an$/Ⓡ/
           - xform/[üuv]e$/Ⓣ/
           - xform/un$/Ⓨ/
           - xform/^sh/Ⓤ/
           - xform/^ch/Ⓘ/
           - xform/^zh/Ⓥ/
           - xform/uo$/Ⓞ/
           - xform/ie$/Ⓟ/
           - xform/(.)i?ong$/$1Ⓢ/
           - xform/ing$|uai$/Ⓚ/
           - xform/(.)ai$/$1Ⓓ/
           - xform/(.)en$/$1Ⓕ/
           - xform/(.)eng$/$1Ⓖ/
           - xform/[iu]ang$/Ⓛ/
           - xform/(.)ang$/$1Ⓗ/
           - xform/ian$/Ⓜ/
           - xform/(.)an$/$1Ⓙ/
           - xform/(.)ou$/$1Ⓩ/
           - xform/[iu]a$/Ⓧ/
           - xform/iao$/Ⓝ/
           - xform/(.)ao$/$1Ⓒ/
           - xform/ui$/Ⓥ/
           - xform/in$/Ⓑ/
        */
        result.add(new Xfrom( "^([aoe])(ng)?$", "$1$1$2"));
        result.add(new Xfrom("iu$", "Ⓠ"));
        result.add(new Xfrom("(.)ei$", "$1Ⓦ"));
        result.add(new Xfrom("[üu]an$", "Ⓡ"));
        result.add(new Xfrom("[üuv]e$", "Ⓣ"));
        result.add(new Xfrom("un$", "Ⓨ"));
        result.add(new Xfrom("^sh", "Ⓤ"));
        result.add(new Xfrom("^ch", "Ⓘ"));
        result.add(new Xfrom("^zh", "Ⓥ"));
        result.add(new Xfrom("uo$", "Ⓞ"));
        result.add(new Xfrom("ie$", "Ⓟ"));
        result.add(new Xfrom("(.)i?ong$", "$1Ⓢ"));
        result.add(new Xfrom("ing$|uai$", "Ⓚ"));
        result.add(new Xfrom("(.)ai$", "$1Ⓓ"));
        result.add(new Xfrom("(.)en$", "$1Ⓕ"));
        result.add(new Xfrom("(.)eng$", "$1Ⓖ"));
        result.add(new Xfrom("[iu]ang$", "Ⓛ"));
        result.add(new Xfrom("(.)ang$", "$1Ⓗ"));
        result.add(new Xfrom("ian$", "Ⓜ"));
        result.add(new Xfrom("(.)an$", "$1Ⓙ"));
        result.add(new Xfrom("(.)ou$", "$1Ⓩ"));
        result.add(new Xfrom("[iu]a$", "Ⓧ"));
        result.add(new Xfrom("iao$", "Ⓝ"));
        result.add(new Xfrom("(.)ao$", "$1Ⓒ"));
        result.add(new Xfrom("ui$", "Ⓥ"));
        result.add(new Xfrom("in$", "Ⓑ"));

        //  - xlit/ⓆⓌⓇⓉⓎⓊⒾⓄⓅⓈⒹⒻⒼⒽⒿⓀⓁⓏⓍⒸⓋⒷⓃⓂ/qwrtyuiopsdfghjklzxcvbnm/
        result.add(new Xlit());
        return result;
    }





}


