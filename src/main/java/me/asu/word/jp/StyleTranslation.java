package me.asu.word.jp;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.function.IntPredicate;

import lombok.Data;
import me.asu.util.Strings;

@Data
public class StyleTranslation {
    public static boolean needChangeStyle(char c) {
        // JISコードの配列で取得
        int targetChar = getSJISByte(c);

        // 半角文字OK(『?』以外)
        if ((0x20 <= targetChar && targetChar <= 0x3D) || (0x40 <= targetChar
                && targetChar <= 0x7E) || '\t' == targetChar) {
            return false;
        }
        // 半角文字『?』について
        else if (targetChar == 0x3F) {
            // 入力文字が『?』であればOK
            if ("?".equals(String.valueOf(c))) {
                return false;
            }
            // NG
            else {
                return true;
            }
        }
        // 半角カナOK
        else if (0xA1 <= targetChar && targetChar <= 0xDF) {
            return false;
        }
        // 全角非漢字OK
        else if (0x8140 <= targetChar && targetChar <= 0x84BE) {
            return false;
        }
        // 第一水準OK
        else if (0x889F <= targetChar && targetChar <= 0x9872) {
            return false;
        }
        // 第二水準OK
        else if ((0x989F <= targetChar && targetChar <= 0x9FFC) || (0xE040 <= targetChar
                && targetChar <= 0xEAA4)) {
            return false;
        }
        // NG
        else {
            return true;
        }
    }

    /**
     * 第2水準の文字種チェック<br /> 半角英数記号・全角英数記号・全角カタカナ・全角ひらがな・漢字（第一水準・第二水準）は許容する。
     *
     * @param str チェック対象文字列
     * @return true:エラーあり/false:エラーなし
     */
    public static boolean checkForbiddenCharacter(String str) {
        char[] charArray = str.toCharArray();
        for (char c : charArray) {
            // JISコードの配列で取得
            int targetChar = getSJISByte(c);

            // 半角文字OK(『?』以外)
            if ((0x20 <= targetChar && targetChar <= 0x3D) || (0x40 <= targetChar
                    && targetChar <= 0x7E)) {
                continue;
            }
            // 半角文字『?』について
            else if (targetChar == 0x3F) {
                // 入力文字が『?』であればOK
                if ("?".equals(String.valueOf(c))) {
                    continue;
                }
                // NG
                else {
//					System.out.println("" + c + " " + Integer.toHexString(c));
                    return true;
                }
            }
            // 半角カナOK
            else if (0xA1 <= targetChar && targetChar <= 0xDF) {
                continue;
            }
            // 全角非漢字OK
            else if (0x8140 <= targetChar && targetChar <= 0x84BE) {
                continue;
            }
            // 第一水準OK
            else if (0x889F <= targetChar && targetChar <= 0x9872) {
                continue;
            }
            // 第二水準OK
            else if ((0x989F <= targetChar && targetChar <= 0x9FFC) || (0xE040 <= targetChar
                    && targetChar <= 0xEAA4)) {
                continue;
            }
            // NG
            else {
//				System.out.println("" + c + " " + Integer.toHexString(c));
                return true;
            }
        }
        return false;
    }

    /**
     * Shift-JISでバイト変換
     *
     * @param c 変換対象文字
     * @return 変換後文字列
     */
    private static int getSJISByte(char c) {
        int targetChar = 0;
        try {
            // MS932でバイト変換
            byte[] byteArray = String.valueOf(c).getBytes("MS932");
            if (byteArray.length <= 1) {
                // 半角文字
                targetChar = (byteArray[0] & 0xFF);
            } else {
                // 全角文字
                targetChar = (((byteArray[0] & 0xFF) * 0x100) + (byteArray[1] & 0xFF));
            }
        } catch (Exception e) {
        }
        return targetChar;
    }

    /**
     * strが全てcharTypesの文字で構成されているか
     *
     * @param str       チェック対象文字列
     * @param charTypes 許容する文字
     * @return true:エラーあり/false:エラーなし
     */
    public static boolean isConsistingWithCharSet(String str, CharType... charTypes) {
        for (char c : str.toCharArray()) {
            // JISコードの配列で取得
            int targetChar = getSJISByte(c);

            // 対象の文字が与えられた文字種でなければfalse
            if (Arrays.stream(charTypes).noneMatch(p -> p.contains(targetChar))) {
                return false;
            }
        }
        return true;
    }

    public static boolean isConsistingWithCharSet(char c, CharType... charTypes) {
        // JISコードの配列で取得
        int targetChar = getSJISByte(c);

        // 対象の文字が与えられた文字種でなければfalse
        if (Arrays.stream(charTypes).noneMatch(p -> p.contains(targetChar))) {
            return false;
        }
        return true;
    }

    public enum CharType {
        // 半角英数記号
        HALFWIDTH_ALPHANUMERICSYMBOL(p -> ((0x20 <= p && p <= 0x3D) || (0x40 <= p && p <= 0x7E)) || (p == 0x3F && "?".equals(String.valueOf((char) p)))),

        // 半角カタカナ
        HALFWIDTH_KATAKANA(p -> 0xA1 <= p && p <= 0xDF),

        // 全角第一水準漢字
        FULLWIDTH_FIRSTLEVEL_KANJI(p -> 0x889F <= p && p <= 0x9872),

        // 全角第二水準漢字
        FULLWIDTH_SECONDLEVEL_KANJI(p -> (0x989F <= p && p <= 0x9FFC) || (0xE040 <= p && p <= 0xEAA4)),

        // 全角その他
        FULLWIDTH_OTHERS(p -> 0x8140 <= p && p <= 0x84BE);

        private final IntPredicate predicate;

        CharType(IntPredicate predicate) {
            this.predicate = predicate;
        }

        public boolean contains(int num) {
            return predicate.test(num);
        }
    }


    String src;
    String dest;
    boolean changed = false;
    String changeMark;
    int rownum = 0;
    StringBuilder remark = new StringBuilder();
    StringBuilder destBuilder = new StringBuilder();
    public StyleTranslation(String src, int rownum) {
        this.src = src;
        this.rownum = rownum < 0 ? 0:rownum;
    }

    public void reset(String str, int rownum) {
        this.src = str;
        dest = null;
        changed = false;
        changeMark = null;
        this.rownum = rownum < 0 ? 0:rownum;
        remark.setLength(0);
        destBuilder.setLength(0);
    }
    public void changeStyle(boolean replaceWithKatakana, String katakana) {
        if (Strings.isEmpty(src)) {
            return;
        }


        char[] chars = src.toCharArray();
        boolean hasCannotChange = false;
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            if (needChangeStyle(c)) {
                changed = true;
                String s = JapaneseHZStyleOldToNew.toNewStyle(String.valueOf(c));
                if (s == null) {
                    // 沒有可以轉換的字
                    hasCannotChange = true;
                    if (replaceWithKatakana) {
                        remark.append("ERROR").append(",")
                                .append(rownum).append(",")
                                .append(src, 0, i).append("[").append(c).append("]").append(src.substring(i + 1)).append(",NOT FOUND,").append(katakana).append(";");
                        break;
                    } else {
                        remark.append("ERROR").append(",")
                                .append(rownum).append(",")
                                .append(c).append(",NOT FOUND").append(";");
                        destBuilder.append(c);
                    }
                } else {
                    // 可以替換
                    destBuilder.append(s);
                    remark.append("INFO").append(",")
                            .append(rownum).append(",")
                            .append(c).append(",").append("CONVERTED,").append(s).append(";");
                }
            } else {
                destBuilder.append(c);
            }
        }

        if (hasCannotChange && replaceWithKatakana) {
            dest = katakana;
        } else {
            dest = destBuilder.toString();
        }
        changeMark = remark.toString();
    }

    public static void main(String[] args) throws IOException {

//		String x = "回";
//		Files.write(Paths.get("output/a.txt"), x.getBytes());
//		Files.write(Paths.get("output/b.txt"), x.getBytes("MS932"));
//		boolean b = checkForbiddenCharacter(x);
//		System.out.println("回 = " + b);

//        StyleTranslation st = new StyleTranslation("特殊字符: 隆朗№＇−");
//        st.changeStyle(false, null);
//        System.out.println("st = " + st.getDest());

//		StyleTranslation st = new StyleTranslation("冬天吃圓圎冰棍，會發達,发达");
//		st.changeStyle(false, "");
//		System.out.println("st = " + st);

//		StyleTranslation st2 = new StyleTranslation("圓圎");
//		StyleTranslation st2 = new StyleTranslation("囬迴");
//		st2.changeStyle(true, "カタカナ");
//		System.out.println("st2 = " + st2);

//		StyleTranslation st3 = new StyleTranslation("龙马");
//		st3.changeStyle(true, "リョマ");
//		System.out.println("st3 = " + st3);
        String[] files = {
                "out/jp-out.txt",
                "out/jp-out2.txt",
                "out/jp-out3.txt",
                "out/jp-out4.txt",
//                "out/jp-out5.txt",
//                "out/jp-out6.txt",
        };
        try(BufferedWriter writer = Files.newBufferedWriter(Paths.get("out\\a.txt"), StandardCharsets.UTF_8)) {
            StyleTranslation st = new StyleTranslation("",0);
            int i = 0;
            for (String file : files) {
                System.out.println("Process " + file);
                List<String> lines = Files.readAllLines(Paths.get(file), StandardCharsets.UTF_8);
                //                    boolean found = checkForbiddenCharacter(line);
                //                    if (found) {
                //                        System.out.println(line + " => " + found);
                //                    }
                for ( String line : lines) {
                    i++;
                    st.reset(line, i);
                    st.changeStyle(false, null);

                    if (st.isChanged()) {
                        System.out.println(line + " => " + st.getChangeMark());
                        writer.write(line + " => " + st.getDest() + ":" + st.getChangeMark());
                        writer.write('\n');
                    } else {
                        System.out.println(">>> " + line);
                        if (st.getDest() != null) {
                            System.out.println("<<< " + st.getDest());
                            writer.write(st.getDest());
                            writer.write('\n');
                        }
                       // System.out.println((i+1) + " is OK. " + st.getChangeMark());
                    }
                }
            }

        }
        System.out.println("Done!");

    }
}
