//import static org.slf4j.LoggerFactory.getLogger;
//
//import com.ibm.icu.text.Transliterator;
//
//import java.time.LocalDate;
//
//import org.apache.commons.lang3.NotImplementedException;
//import org.apache.commons.lang3.StringUtils;
//import org.slf4j.Logger;
//
//public class TranslateUtil {
//
//    private static final Logger log = getLogger(TranslateUtil.class);
//
//    /**
//     * 全角→半角
//     *
//     * まずは全角を半角に変換する方法です。 次のサンプルプログラムのように、半角文字を含む文字列の変換を行ってくれます。
//     */
//    static Transliterator F2H_TRANSLITERATOR = Transliterator.getInstance("Fullwidth-Halfwidth");
//
//    /**
//     * 半角→全角
//     * <p>
//     * 次は半角→全角の変換です。 Transliterator の getInstance の引数を変更しています。
//     */
//    static Transliterator H2F_TRANSLITERATOR = Transliterator.getInstance("Halfwidth-Fullwidth");
//    /**
//     * カタカナ→ひらがな
//     * <p>
//     * カタカナをひらがなに変換することもできます。 次の例のように、カタカナは全角でも半角のどちらからでも対応しています。
//     */
//
//    static Transliterator K2H_TRANSLITERATOR = Transliterator.getInstance("Katakana-Hiragana");
//
//    /**
//     * ひらがな→カタカナ
//     * <p>
//     * ひらがなを全角カタカナに変換することもできます。
//     */
//    static Transliterator H2K_TRANSLITERATOR = Transliterator.getInstance("Hiragana-Katakana");
//
//    /**
//     * ひらがな→ローマ字
//     * <p>
//     * ひらがなをローマ字に変換することもできます。 但し小文字のローマ字に変換されるので、大文字にする必要があればもう一段階の変換が必要です。
//     */
//
//    static Transliterator H2L_TRANSLITERATOR = Transliterator.getInstance("Hiragana-Latin");
//
//    /**
//     * 全角→半角
//     * ex. アイウガギグ１２３①￥％＄ -> ｱｲｳｶﾞｷﾞｸﾞ123①¥%$
//     * <p>
//     * 上のサンプルにわざと対応する半角文字がないと思われる「①」という文字を入れてみました。
//     * 結果の通り「①」という文字は変換が行われません。
//     * 作るシステムで「①」を「(1)」に変換するなどの要件がある場合は、IUJ4Jを利用する前後で自力でやる必要がありそうです。
//     * <p>
//     * また知っておいて頂きたいのは Java 1.6 以降で利用できるようになった java.text.Normalizer クラスの存在です。
//     * java.text.Normalizer クラスを利用すれば、全角英数字であれば半角に変換することができます。
//     * どのような文字種類を変換するのかを気にした上で、ICU4J と Normalizer クラスを使い分けた方が良いでしょう。
//     *
//     * @param source
//     * @return
//     */
//    public static String fullToHalf(String source) {
//        if (StringUtils.isEmpty(source)) {
//            return source;
//        }
//        return F2H_TRANSLITERATOR.transliterate(source);
//    }
//
//    /**
//     * 半角→全角
//     * ex. ｱｲｳｴｵｶﾞｷﾞｸﾞｹﾞｺﾞ -> アイウエオガギグゲゴ
//     *
//     * @param source
//     * @return
//     */
//    public static String halfToFull(String source) {
//        if (StringUtils.isEmpty(source)) {
//            return source;
//        }
//        return H2F_TRANSLITERATOR.transliterate(source);
//    }
//
//
//    /**
//     * カタカナ→ひらがな
//     * ex. ｱｲｳｴｵｶﾞｷﾞｸﾞｹﾞｺﾞ アイウエオガギグゲゴ -> あいうえおがぎぐげご あいうえおがぎぐげご
//     *
//     * @param source
//     * @return
//     */
//    public static String katakanaToHiragana(String source) {
//        if (StringUtils.isEmpty(source)) {
//            return source;
//        }
//        return K2H_TRANSLITERATOR.transliterate(source);
//    }
//
//    /**
//     * ひらがな→カタカナ
//     * ex. あいうえおがぎぐげご -> アイウエオガギグゲゴ
//     *
//     * @param source
//     * @return
//     */
//    public static String hiraganaToKatakana(String source) {
//        if (StringUtils.isEmpty(source)) {
//            return source;
//        }
//        return H2K_TRANSLITERATOR.transliterate(source);
//    }
//
//    /**
//     * ひらがな→ローマ字
//     * ex. あいうえおがぎぐげご -> aiueogagigugego
//     *
//     * @param source
//     * @return
//     */
//    public static String hiraganaToLatin(String source) {
//        if (StringUtils.isEmpty(source)) {
//            return source;
//        }
//        return H2L_TRANSLITERATOR.transliterate(source);
//    }
//
//
//    public static void main(String[] args) {
//        String x = "ｱｲｳｴｵｶﾞｷﾞｸﾞｹﾞｺﾞ123456/正常的汉字呢。";
//        System.out.println("h2f(x)   = " + halfToFull(x));
////		int Diff = 'Ａ'  - 'A';
////		System.out.println("Diff = " + Diff);
////		JapaneseCalendar japaneseCalendar= new JapaneseCalendar();
////		DateFormat japaneseDateFormat = japaneseCalendar.getDateTimeFormat(DateFormat.LONG, -1, Locale.JAPAN);
////		String today = japaneseDateFormat.format(new Date());
////		System.out.println("today is: " + today.replaceAll("/", ""));
//
//        int i = toEraYear(2019);
//        int j = toEraYear(2020);
//        System.out.println("2019 = " + i);
//        System.out.println("2020 = " + j);
//    }
//
//    public static final LocalDate[] KNOWN_ERAS = new LocalDate[]{
//            LocalDate.of(1868, 1, 1),
//            LocalDate.of(1912, 7, 30),
//            LocalDate.of(1926, 12, 25),
//            LocalDate.of(1989, 1, 8),
//            LocalDate.of(2019, 5, 1)
//    };
//
//
//    public static final LocalDate[] KNOWN_ERAS_SIMPLE = new LocalDate[]{
//            LocalDate.of(1868, 1, 1),
//            LocalDate.of(1912, 1, 1),
//            LocalDate.of(1926, 1, 1),
//            LocalDate.of(1989, 1, 1),
//            LocalDate.of(2019, 1, 1)
//    };
//
//    public static final int toEraYear(int year) {
//        int[] earYearPoint = new int[]{2019, 1989, 1926, 1912, 1868};
//        for (int i = 0; i < earYearPoint.length; i++) {
//            int point = earYearPoint[i];
//            if (year >= point) {
//                return year - point + 1;
//            }
//        }
//
//        throw new NotImplementedException("No implement for this year:" + year);
//    }
//
//
//}
