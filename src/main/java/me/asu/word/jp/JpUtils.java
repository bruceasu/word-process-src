package me.asu.word.jp;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import lombok.Data;
import me.asu.util.Streams;

@Data
public class JpUtils {

    public static boolean isSymbol(char c) {
        // JISコードの配列で取得
        int targetChar = getSJISByte(c);

        // 半角文字OK(『?』以外)
        boolean isSymbol1 = 0x20 <= targetChar && targetChar <= 0x3D;
        boolean isSymbol2 = 0x40 <= targetChar && targetChar <= 0x7E;
        return isSymbol1 || isSymbol2;
    }

    public static boolean isHalf(char c) {
        // JISコードの配列で取得
        int targetChar = getSJISByte(c);

        return 0xA1 <= targetChar && targetChar <= 0xDF;
    }

    public static boolean isFullPunt(char c) {
        // JISコードの配列で取得
        int targetChar = getSJISByte(c);

        return 0x8140 > targetChar || targetChar > 0x84BE;
    }

    public static boolean isLevel1(char c) {
        // JISコードの配列で取得
        int targetChar = getSJISByte(c);

        return 0x889F <= targetChar && targetChar <= 0x9872;
    }

    public static boolean isLevel2(char c) {
        // JISコードの配列で取得
        int targetChar = getSJISByte(c);

        boolean isPart1 = 0x989F <= targetChar && targetChar <= 0x9FFC;
        boolean isPart2 = 0xE040 <= targetChar && targetChar <= 0xEAA4;
        return isPart1 || isPart2;
    }

    public static boolean needChangeStyle(char c) {
        // JISコードの配列で取得
        int targetChar = getSJISByte(c);

        // 半角文字OK(『?』以外)
        if ((0x20 <= targetChar && targetChar <= 0x3D) || (0x40 <= targetChar
                && targetChar <= 0x7E)) {
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
        else if ((0x989F <= targetChar && targetChar <= 0x9FFC) || (
                0xE040 <= targetChar
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
            if ((0x20 <= targetChar && targetChar <= 0x3D) || (
                    0x40 <= targetChar
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
            else if ((0x989F <= targetChar && targetChar <= 0x9FFC) || (
                    0xE040 <= targetChar
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
                targetChar = (((byteArray[0] & 0xFF) * 0x100) + (byteArray[1]
                        & 0xFF));
            }
        } catch (Exception e) {
        }
        return targetChar;
    }

//	/**
//	 * strが全てcharTypesの文字で構成されているか
//	 *
//	 * @param str       チェック対象文字列
//	 * @param charTypes 許容する文字
//	 * @return true:エラーあり/false:エラーなし
//	 */
//	public static boolean isConsistingWithCharSet(String str, CharType... charTypes)
//	{
//		for (char c : str.toCharArray()) {
//			// JISコードの配列で取得
//			int targetChar = getSJISByte(c);
//
//			// 対象の文字が与えられた文字種でなければfalse
//			if (Arrays.stream(charTypes).noneMatch(p -> p.contains(targetChar))) {
//				return false;
//			}
//		}
//		return true;
//	}
//
//	public static boolean isConsistingWithCharSet(char c, CharType... charTypes)
//	{
//		// JISコードの配列で取得
//		int targetChar = getSJISByte(c);
//
//		// 対象の文字が与えられた文字種でなければfalse
//		if (Arrays.stream(charTypes).noneMatch(p -> p.contains(targetChar))) {
//			return false;
//		}
//		return true;
//	}
//
//	public enum CharType
//	{
//		// 半角英数記号
//		HALFWIDTH_ALPHANUMERICSYMBOL(
//				p -> ((0x20 <= p && p <= 0x3D) || (0x40 <= p && p <= 0x7E)) || (p == 0x3F
//						&& "?".equals(String.valueOf((char) p)))),
//
//		// 半角カタカナ
//		HALFWIDTH_KATAKANA(p -> 0xA1 <= p && p <= 0xDF),
//
//		// 全角第一水準漢字
//		FULLWIDTH_FIRSTLEVEL_KANJI(p -> 0x889F <= p && p <= 0x9872),
//
//		// 全角第二水準漢字
//		FULLWIDTH_SECONDLEVEL_KANJI(
//				p -> (0x989F <= p && p <= 0x9FFC) || (0xE040 <= p && p <= 0xEAA4)),
//
//		// 全角その他
//		FULLWIDTH_OTHERS(p -> 0x8140 <= p && p <= 0x84BE);
//
//		private final IntPredicate predicate;
//
//		CharType(IntPredicate predicate)
//		{
//			this.predicate = predicate;
//		}
//
//		public boolean contains(int num)
//		{
//			return predicate.test(num);
//		}
//	}


    public static void main(String[] args) throws IOException {
        String in = "src/main/resources/日本用字.txt";
//        String in = "src/main/resources/merged-j.txt";
        String out1 = "src/main/resources/日本汉字水准1.txt";
        String out2 = "src/main/resources/日本汉字水准2.txt";
        String out3 = "src/main/resources/日本汉字其他.txt";
        int c1 = 0, c2= 0, c3= 0;
        try (
                Reader r = Streams.fileInr(in, "UTF-8");
                Writer w1 = Streams.fileOutw(out1);
                Writer w2 = Streams.fileOutw(out2);
                Writer w3 = Streams.fileOutw(out3)) {
            List<String> strings = Files.readAllLines(Paths.get(in));
            for (String string : strings) {
                String[] split = string.split("\\s+");
                if (split[0].length() > 1) {
                    w3.write(string);
                    w3.write("\r\n");
                    c3++;
                } else {
                    char c = split[0].charAt(0);

                    if(isLevel1(c)) {
                        w1.write(string);
                        w1.write("\r\n");
                        c1++;
                    } else if(isLevel2(c)) {
                        w2.write(string);
                        w2.write("\r\n");
                        c2++;
                    } else if (c == '\r' || c == '\n'){
                        continue;
                    } else {
                        w3.write(string);
                        w3.write("\r\n");
                        c3++;
                    }
                }
            }
        } finally {
            System.out.println("Leve1: " + c1);
            System.out.println("Leve2: " + c2);
            System.out.println("Other: " + c3);
            System.out.println("DONE!");
        }

    }
}
