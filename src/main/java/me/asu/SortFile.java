package me.asu;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Pattern;

public class SortFile {

    private static final int CHUNK_SIZE = 100000;  // 每个小块的大小，具体根据内存情况调整

    public static void main(String[] args) {
        String  inputFile    = null;
        String  outputFile   = null;
        String  charset      = Charset.defaultCharset().name();
        int     sortColumn   = -1;
        int     sortType     = 1;  // 1: 文本, 2: 数字
        Pattern splitPattern = Pattern.compile("\\s+"); // 默认按空白字符（空格、制表符等）分隔
        boolean help         = false;
        // 解析命令行参数
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            switch (arg) {
                case "-h":
                case "--help":
                    help = true;
                    break;
                case "-i":
                case "--input":
                    if (i + 1 < args.length) {
                        inputFile = args[i + 1];
                        i++;
                    } else {
                        System.err.println("Need the input file path.");
                        showHelp();
                        System.exit(1);
                    }
                    break;
                case "-o":
                case "--output":
                    if (i + 1 < args.length) {
                        outputFile = args[i + 1];
                        i++;
                    } else {
                        System.err.println("Need the output file path.");
                        showHelp();
                        System.exit(1);
                    }
                    break;
                case "--charset":
                    if (i + 1 < args.length) {
                        charset = args[i + 1];
                        i++;
                    } else {
                        System.err.println("Need the charset.");
                        showHelp();
                        System.exit(1);
                    }
                    break;
                case "-c":
                case "--column":
                    if (i + 1 < args.length) {
                        sortColumn = Integer.parseInt(args[i + 1]);
                        i++;
                    } else {
                        System.err.println("Need the column.");
                        showHelp();
                        System.exit(1);
                    }
                    break;
                case "-t":
                case "--column-type":
                    if (i + 1 < args.length) {
                        sortType = Integer.parseInt(args[i + 1]);
                        i++;
                    } else {
                        System.err.println("Need the column type.");
                        showHelp();
                        System.exit(1);
                    }
                    break;
                case "-d":
                    if (i + 1 < args.length) {
                        splitPattern = Pattern.compile(args[i + 1]);
                        i++;
                    } else {
                        System.err.println("Need the split pattern.");
                        showHelp();
                        System.exit(1);
                    }
                    break;
            }

        }

        if (help) {
            showHelp();
            System.exit(2);
        }

        try {
            // 读取输入文件，如果未提供则使用标准输入
            BufferedReader reader;
            if (inputFile != null) {
                reader = new BufferedReader(new InputStreamReader(new FileInputStream(inputFile), charset));
            } else {
                reader = new BufferedReader(new InputStreamReader(System.in, charset));
            }

            List<File>   tempFiles    = new ArrayList<>();
            List<String> currentChunk = new ArrayList<>();

            // 分块读取文件，处理每一块
            String line;
            while ((line = reader.readLine()) != null) {
                currentChunk.add(line);
                if (currentChunk.size() >= CHUNK_SIZE) {
                    File tempFile = processChunk(currentChunk, sortColumn, sortType, splitPattern);
                    tempFiles.add(tempFile);
                    currentChunk.clear(); // 清空当前块
                }
            }

            // 处理剩余的行
            if (!currentChunk.isEmpty()) {
                File tempFile = processChunk(currentChunk, sortColumn, sortType, splitPattern);
                tempFiles.add(tempFile);
            }
            reader.close();

            // 合并所有小文件
            mergeFiles(tempFiles, outputFile, charset);
            System.out.println("Write the result to " + outputFile);
        } catch (IOException e) {
            System.err.println("Error processing the file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void showHelp() {
        System.out.println("Usage: sort [options]");
        System.out.println("  -i,--input <FILE> the input file");
        System.out.println("  -o,--output <FILE> the output file");
        System.out.println("     --charset <CHARSET> the file charset");
        System.out.println("  -c,--column <NUM> the sort column that begins zero.");
        System.out.println("  -t,--column-type <NUM> 1: text 2:number.");
        System.out.println("  -d <PATTERN> the splitter pattern.");
    }

    private static File processChunk(List<String> chunk, int sortColumn, int sortType, Pattern splitPattern) throws IOException {
        // 对当前块进行排序
        Collections.sort(chunk, new Comparator<String>() {
            @Override
            public int compare(String line1, String line2) {
                if (sortColumn < 0) {
                    return line1.compareTo(line2);
                }
                String[] parts1 = splitPattern.split(line1);
                String[] parts2 = splitPattern.split(line2);

                // 确保排序列在行中存在
                if (sortColumn >= parts1.length || sortColumn >= parts2.length) {
                    return 0;
                }

                String value1 = parts1[sortColumn];
                String value2 = parts2[sortColumn];

                int i = cmpCol(value1, value2);
                if (i == 0) {
                    return line1.compareTo(line2);
                }
                return i;
            }

            private int cmpCol(String value1, String value2) {
                if (sortType == 2) { // 数字排序
                    try {
                        Double num1 = Double.parseDouble(value1);
                        Double num2 = Double.parseDouble(value2);
                        return -1 * Double.compare(num1, num2);
                    } catch (NumberFormatException e) {
                        // 如果无法转换为数字，作为文本进行比较
                        return value1.compareTo(value2);
                    }
                } else { // 文本排序
                    return value1.compareTo(value2);
                }
            }
        });

        // 将排序后的块写入临时文件
        File           tempFile = File.createTempFile("sorted_chunk", ".tmp");
        BufferedWriter writer   = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(tempFile), Charset.defaultCharset()));
        for (String line : chunk) {
            writer.write(line);
            writer.newLine();
        }
        writer.close();

        return tempFile;
    }

    // FIXME 合并文件有问题
    private static void mergeFiles(List<File> tempFiles, String outputFile, String charset) throws IOException {
        // 使用优先队列合并文件
        PriorityQueue<FileReaderWrapper> pq = new PriorityQueue<>(Comparator.comparing(line -> line.line));

        List<BufferedReader> readers = new ArrayList<>();
        for (File tempFile : tempFiles) {
            BufferedReader reader = new BufferedReader(new FileReader(tempFile));
            readers.add(reader);
            String firstLine = reader.readLine();
            if (firstLine != null) {
                pq.add(new FileReaderWrapper(reader, firstLine));
            }
        }

        BufferedWriter writer;
        if (outputFile != null) {
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFile), charset));
        } else {
            writer = new BufferedWriter(new OutputStreamWriter(System.out, charset));
        }

        // 合并文件
        while (!pq.isEmpty()) {
            FileReaderWrapper wrapper = pq.poll();
            writer.write(wrapper.line);
            writer.newLine();
            String nextLine = wrapper.reader.readLine();
            if (nextLine != null) {
                pq.add(new FileReaderWrapper(wrapper.reader, nextLine));
            }
        }

        writer.close();

        // 删除临时文件
        for (BufferedReader reader : readers) {
            reader.close();
        }
        for (File tempFile : tempFiles) {
            tempFile.delete();
        }
    }

    // 用于优先队列的文件读取包装器
    private static class FileReaderWrapper {
        BufferedReader reader;
        String         line;

        FileReaderWrapper(BufferedReader reader, String line) {
            this.reader = reader;
            this.line = line;
        }
    }
}
