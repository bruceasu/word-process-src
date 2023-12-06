package me.asu.word.jp;

import static org.slf4j.LoggerFactory.getLogger;

import com.csvreader.CsvReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import me.asu.util.Strings;
import org.slf4j.Logger;

public class JapaneseHZStyleOldToNew {

    private static final Logger log = getLogger(JapaneseHZStyleOldToNew.class);
    private static final Map<String, String> MAPPING = new HashMap<>();
    private static final String JP_STYLES_CSV = "jp-styles.csv";

    private static void loadStyleMap() {

        try {
            InputStream in = null;
            // 看看有没有java环境变量
            String property = System.getProperty("jp-style.csv");
            if (!Strings.isEmpty(property)) {
                Path path = Paths.get(property);
                if (Files.isRegularFile(path)) {
                    in = Files.newInputStream(path);
                }
            }
            if (in == null) {
                // 再看看执行目录下是否有此文件
                Path path = Paths.get(JP_STYLES_CSV);
                if (Files.isRegularFile(path)) {
                    in = Files.newInputStream(path);
                }
            }
            if (in == null) {
                // 最后看看 classpath
                in = JapaneseHZStyleOldToNew.class.getClassLoader().getResourceAsStream(JP_STYLES_CSV);
            }
            if (in == null) {
                // 挂了
                log.warn("没有找到 jp-styles.csv 文件，日文汉字新旧字体转换功能不可用。");
                return;
            }

            CsvReader csvReader = new CsvReader(in, StandardCharsets.UTF_8);
            // No.,新字体,旧字体,旧字体,旧字体,旧字体,旧字体,旧字体
            csvReader.readHeaders();
            while (csvReader.readRecord()) {
                int columnCount = csvReader.getColumnCount();
                if (columnCount < 3) {
                    continue;
                }
                String s = csvReader.get(1);
                if (Strings.isEmpty(s)) {
                    log.warn("这一行没有新字形定义。{}", Arrays.toString(csvReader.getValues()));
                    continue;
                }
                if (s.startsWith("MS932:")) {
                    String[] split = s.split(":");
                    if (split.length == 3) {
                        log.info("特殊映射：{}", s);
                        s = String.valueOf((char) Integer.parseInt(split[2], 16));
                        log.info("UNCODE： {}", s);
                    } else if (split.length == 2) {
                        log.warn("这个shift jis 编码没有对应的UNICODE编码，暂时不支持处理。 原文： {}", s);
                        // 暂时没有这个情况，万一有这种情况，则不能在字符层面处理，需要在字节层面处理.
                        // 这样一来，整个处理系统要转成很底层字节系统，而不是java当前的unicode字符系统。
                        continue;
                    } else {
                        // 无法处理
                        continue;
                    }
                }

                for (int i = 2; i < columnCount; i++) {
                    String str = csvReader.get(i);
                    if (Strings.isEmpty(str)) {
                        continue;
                    }
                    MAPPING.put(str, s);
                }

            }
            csvReader.close();

        } catch (IOException e) {
            log.error("处理 jp-styles.csv 文件出现错误，日文汉字新旧字体转换功能不可用。", e);
        } finally {
            log.info("加载{}个旧新映射对。", MAPPING.size());
        }
    }

    static {
        loadStyleMap();
    }


    public static String toNewStyle(String oldStyle) {
        return MAPPING.get(oldStyle);
    }

    public static void main(String[] args) throws UnsupportedEncodingException {
        System.out.println("toNewStyle(\"亞\") = " + toNewStyle("亞"));
        System.out.println("toNewStyle(\"發\") = " + toNewStyle("發"));
        System.out.println("toNewStyle(\"宮\") = " + toNewStyle("宮"));
        // 1088,MS932:817C:FF0D,−
        char x = 0xFF0D;
        String s = String.valueOf(x);
        System.out.println("s = " + s);
        byte[] ms932s = s.getBytes("MS932");
        for (byte b : ms932s) {
            int i = Byte.toUnsignedInt(b);
            System.out.println(Integer.toHexString(i));
        }
    }
}
