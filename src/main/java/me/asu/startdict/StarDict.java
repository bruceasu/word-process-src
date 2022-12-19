package me.asu.startdict;

import com.csvreader.CsvWriter;
import com.csvreader.CsvWriter.Letters;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

public class StarDict {

    final static int MAX_WORD=256; // 最长输入单词字符数
    final static int MAX_KEYS=27;  // 26个字母+"-"开头的后缀
    final static int SIZEINT=4;

    /**
     * 重写了Inpustream 中的skip(long n) 方法，将数据流中起始的n 个字节跳过
     * 参考：http://blog.csdn.net/ranxiedao/article/details/7787342
     *
     * @param inputStream
     * @param n
     * @return
     */
    private static long skipBytesFromStream(InputStream inputStream, long n) {
        // SKIP_BUFFER_SIZE is used to determine the size of
        long remaining = n;
        // skipBuffer
        int SKIP_BUFFER_SIZE = 2048; // skipBuffer is initialized in
        // skip(long), if needed.
        byte[] skipBuffer = null;
        int nr = 0;
        if (skipBuffer == null) {
            skipBuffer = new byte[SKIP_BUFFER_SIZE];
        }
        byte[] localSkipBuffer = skipBuffer;
        if (n <= 0) {
            return 0;
        }
        while (remaining > 0) {
            try {
                nr = inputStream.read(localSkipBuffer, 0, (int) Math.min(
                        SKIP_BUFFER_SIZE, remaining));
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (nr < 0) {
                break;
            }
            remaining -= nr;
        }
        return n - remaining;
    }


    Path infoPath;// = Paths.get(dir, dict+ ".ifo");
    Path idxPath;//  = Paths.get(dir,dict+ ".idx");  // idx文件路径
    Path dictPath;// = Paths.get(dir,dict+".dict"); // dict文件路径
    Path dictDzPath;// = Paths.get(dir,dict+".dict.dz"); // dict文件路径

    public InputStream isIdx        = null;  // 读取idx文件时所要的流
    public InputStream isDict       = null; // 读取dict文件时所要的流
    public Info info = new Info();

    public void convertToCsv(String file) throws IOException {
        System.out.println("Convert dict to csv...");
        CsvWriter writer = new CsvWriter(file, Letters.COMMA, StandardCharsets.UTF_8);
        List<Idx> idxList = new ArrayList<>();
        int samTypeIndex = info.getSamTypeIndex(SameType.m);
        int cnt=0;
        do {
            Idx idx = readIdx();
            if (idx == null) break;
            cnt++;
            String word = idx.getWord();
            String meaning = getData(idx);
            String[] split = meaning.split("\0");
            if (samTypeIndex>=split.length) {
                System.err.println("Can't find the meaning field of " + word);
                continue;
            }
            if (cnt % 1000 == 0) { System.out.println("Process " + cnt + " phrases."); }
            writer.writeRecord(new String[]{word, split[samTypeIndex].replaceAll("\\r|\\n", ";").trim()});
        } while(true);

        writer.close();
        System.out.println("Process " + cnt + " phrases.");
        System.out.println("Saved to " + file);
    }

    private ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    private Idx readIdx() {
        try {
            // 读取单词，对每个字母开头的单词都进行搜索，最多考虑256个字符的单词，
            // 读到单词结束符\0时赋值表达式的值就不满足while条件而退出
            int index = -1;
            int len = 0;
            Idx idx = new Idx();

            while (true) {
                index = isIdx.read();
                if (index == -1) {
                    return null;
                }
                if ((index != 0) && (len < MAX_WORD)) {
                   buffer.write(index);
                    len++;
                } else {
                    break;
                }
            }

            // 转换为JAVA字符串
            // 此处不用再需要像c/c++那样去掉了最后那个结束符了

            idx.setRawData(buffer.toByteArray());
            buffer.reset();

            // 读取偏移量值
            int value = 0;
            for (int i = 0; i < SIZEINT; i++) {
            // 将 4 个 byte 转换为 int
                int shift = (4 - 1 - i) * 8;
                index = isIdx.read();
                if (index == -1) {
                    return null;
                }
                value += (index & 0x00FF) << shift;
            }

           idx.setOffset(value);
            // 读取区块大小值
            value = 0;
            for (int i = 0; i < SIZEINT; i++) {
                // 将 4 个 byte 转换为 int
                int shift = (4 - 1 - i) * 8;
                index = isIdx.read();
                if (index == -1) {
                    return null;
                }
                value += (index & 0x00FF) << shift;
            }
            idx.setLength(value);
            return idx;
        } catch (Exception e) {
            System.out.println("idx file read error!");
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 通过偏移位置offset和长度length 来从dict文件中获取data内容UTF-8编码的字符
     *
     */
    public String getData(Idx idx) {
        long oft = idx.getOffset();
        int len = idx.getLength();
        long skip;
        byte[]  dataBuf = new byte[len];
//        System.out.println("This word("+idx.getWord()+")'s " + "offset:" + oft + "len:" + len);

        try {
            isDict.reset();
            long dictLength = isDict.available();
            if (dictLength < oft + len) {
                System.out.println("No so much value data! " + dictLength);
            }
            skip = skipBytesFromStream(isDict, oft);
            if (skip != oft) {
                System.out.println("Skip" + skip + " dict file error!");
            }
            if (isDict.read(dataBuf) == -1) {
                System.out.println("Arrive at the end of file!");
            }

        } catch (Exception e) {
            dataBuf = null;
            System.out.println("dict file read error!");
            e.printStackTrace();
        }
        if (dataBuf == null) {
            return null;
        }

        return new String(dataBuf,StandardCharsets.UTF_8);
    }


    public void openDictFile() {
        try {
            System.out.println("Open dict file....!");
            if(dictPath!= null) {
                isDict = new BufferedInputStream(
                        Files.newInputStream(dictPath));
            } else if (dictDzPath != null) {
                GZIPInputStream in = new GZIPInputStream(Files.newInputStream(dictDzPath));
                byte[] bytes = IOUtils.readNBytes(in, Integer.MAX_VALUE);
                isDict = new BufferedInputStream(new ByteArrayInputStream(bytes));
            }
            isDict.mark(isDict.available() + 1);
            if (!isDict.markSupported()) {
                System.out.println("This stream do not support mark....!");
            }
        } catch (Exception e) {
            System.out.println("Open dict file error!");
            e.printStackTrace();
        }
    }

    public void openIdxFile() {
        try {
            System.out.println("Open index file....!");
            // 读取字典索引文件
            isIdx = new BufferedInputStream(Files.newInputStream(idxPath));
            isIdx.mark(isIdx.available() + 1);
            if (!isIdx.markSupported()) {
                System.out.println("This stream do not support mark....!");
            }
        } catch (Exception e) {
            System.out.println("Open idx file error!");
            e.printStackTrace();
        }
    }

    public void loadInfoFile() {
        try {
        System.out.println("Load info file...");
        info.load(infoPath);
        } catch (Exception e) {
            System.out.println("Open ifo file error!");
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            if(isIdx!=null) isIdx.close();
        } catch (Exception e) {
            System.out.println("Close  idx file error!");
            e.printStackTrace();
        }

        try {
          if (isDict != null)  isDict.close();
        } catch (Exception e) {
            System.out.println("Close dict file error!");
            e.printStackTrace();
        }
    }

    public Info getInfo() {
        return info;
    }

    /**
     * 主函数
     *
     * @param args
     */
    public static void main(String args[]) throws IOException {
        String[] dirList = {
                "stardict-kdic-ec-11w-2.4.2",
                "stardict-oxford-gb-formated-2.4.2",
                "stardict-ProECCE-2.4.2",
                "stardict-quick_eng-zh_CN-2.4.2",
                "stardict-stardict1.3-2.4.2",
                "stardict-xdict-ce-gb-2.4.2",
                "stardict-xdict-ec-gb-2.4.2"};
        for (String s : dirList) {
            Path dir = Paths.get("D:\\06_cpp\\startdict\\sdcv-dict", s);
            StarDict sd = new StarDict();
            Files.list(dir).forEach(p->{
                if(p.toString().endsWith(".ifo")) sd.infoPath = p;
                if(p.toString().endsWith(".dict")) sd.dictPath = p;
                if(p.toString().endsWith(".dict.dz")) sd.dictDzPath = p;
                if(p.toString().endsWith(".idx")) sd.idxPath = p;
            });
            sd.loadInfoFile();
            Info info = sd.getInfo();
            System.out.println(info);
            sd.openIdxFile();
            sd.openDictFile();
            sd.convertToCsv("out\\"+info.bookname+"-dict.csv");
            sd.close();
        }


    }
}
