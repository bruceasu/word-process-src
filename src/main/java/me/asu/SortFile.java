package me.asu;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import org.apache.commons.cli.*;

public class SortFile {

    public static final String simple = "sort-order.txt";
    public static final String cj = "sort-order-cj.txt";
    public static final String tradition = "sort-order-t.txt";
    public static final String simplePhrases = "phrases-freq-s.txt";
    public static final String traditionPhrases = "phrases-freq-t.txt";
    public static final String simpleMixed = "mixed-freq-s.txt";
    public static final String traditionMixed = "mixed-freq-t.txt";

    // 定义命令行参数
    private static Options definedOptions() {
        Option opt_h = new Option("h", "help", false, "打印本信息。");
        Option opt_e = new Option("e", "encoding", true, "文件编码。");
        Option opt_i = new Option("i", "input", true, "输入文件。");
        Option opt_p = new Option("p", "phrase", false, "文件是词库。");
        Option opt_m = new Option("m", "mixed", false, "文件是字词库。");
        Option opt_o = Option.builder("o").hasArg().argName("output")
                .desc("输出文件").build();
        Option opt_t = Option.builder("t").argName("trad").desc("用繁体排序")
                .build();
        Option opt_n = Option.builder("c").hasArg().argName("column")
                .desc("排序列").build();
        Option opt_cj = Option.builder("j").argName("chinese-japanese")
                .desc("用中式日字排序").build();
        Options opts = new Options();
        opts.addOption(opt_h);
        opts.addOption(opt_e);
        opts.addOption(opt_i);
        opts.addOption(opt_o);
        opts.addOption(opt_t);
        opts.addOption(opt_n);
        opts.addOption(opt_p);
        opts.addOption(opt_m);
        opts.addOption(opt_cj);

        return opts;
    }

    // 解析处理命令行参数
    private static CommandLine parseOptions(String[] args) {
        CommandLineParser parser = new DefaultParser();
        CommandLine line = null;
        // 解析命令行参数
        try {
            return parser.parse(definedOptions(), args);
        } catch (ParseException e) {
            System.err.println(e.getMessage());
            System.exit(1);
            return null;
        }
    }

    public static void main(String[] args) throws IOException {
        int col = 0;
        String encoding = "UTF-8";
        String input = null;
        String output = null;
        boolean isTradition = false;
        boolean isOutputToConsole = false;
        boolean isPhrases = false;
        boolean isCj = false;
        boolean isMixed = false;

        CommandLine cmdLine = parseOptions(args);
        if (cmdLine.hasOption('h')) {
            printUsageAndExit();
        }

        if (cmdLine.hasOption('e')) {
            encoding = cmdLine.getOptionValue('e');
        }
        if (cmdLine.hasOption('c')) {
            col = Integer.parseInt(cmdLine.getOptionValue('c'));
        }
        if (cmdLine.hasOption('i')) {
            input = cmdLine.getOptionValue('i');
        }
        if (cmdLine.hasOption('o')) {
            output = cmdLine.getOptionValue('o');
        }
        if (cmdLine.hasOption('t')) {
            isTradition = true;
        }
        if (cmdLine.hasOption('p')) {
            isPhrases = true;
        }
        if (cmdLine.hasOption('m')) {
            isMixed = true;
        }
        if (cmdLine.hasOption('j')) {
            isCj = true;
        }
        if (input == null || input.isEmpty()) {
            printUsageAndExit();
        } else {
            if (!Files.isRegularFile(Paths.get(input))) {
                System.err.println(input + " is not found.");
                System.exit(1);
            }
        }
        if (output == null || output.isEmpty()) {
            isOutputToConsole = true;
        }
        Map<String, Integer> dict = loadSortDict(isTradition, isCj, isPhrases, isMixed);
        Path inPath = Paths.get(input);
        Charset cs = Charset.forName(encoding);
        List<String> lines = Files.readAllLines(inPath, cs);
        Map<Integer, List<String>> result = new TreeMap<>();
        for (String line : lines) {
            String[] split = line.split("\\s+");
            if (split.length > col) {
                String s = split[col];
                Integer idx = dict.get(s);
                if (idx == null) {
                    idx = Integer.MAX_VALUE;
                }
                if (result.containsKey(idx)) {
                    result.get(idx).add(line);
                } else {
                    List<String> list = new LinkedList<>();
                    list.add(line);
                    result.put(idx, list);
                }
            } else {
                System.err.println(
                        "Can't find the sort column[" + col + "]: " + line);
            }
        }
        BufferedWriter writer;
        if (isOutputToConsole) {
            writer = new BufferedWriter(new OutputStreamWriter(System.out));
        } else {
            Path outPath = Paths.get(output);
            writer = Files.newBufferedWriter(outPath, cs);
        }
        Collection<List<String>> values = result.values();
        for (List<String> value : values) {
            Collections.sort(value);
            for (String s : value) {
                writer.write(s);
                writer.write("\n");
            }
        }
        writer.close();
        System.out.println("DONE.");
    }

    private static void printUsageAndExit() {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("Sort", definedOptions());
        System.exit(1);
    }

    public static Map<String, Integer> loadSortDict(boolean isTradition, boolean isCj,
                                                    boolean isPhrases,
                                                    boolean isMixed)
            throws IOException {
        String name = simple;
        if (isCj) {
            if (isPhrases) {
                if (isMixed) {
                    name = simpleMixed;
                } else {
                    name = simplePhrases;
                }
            } else {
                name = SortFile.cj;
            }
        } else if (isTradition) {

            if (isPhrases) {
                if (isMixed) {
                    name = traditionMixed;
                } else {
                    name = traditionPhrases;
                }
            } else {
                name = SortFile.tradition;
            }
        } else {

            if (isPhrases) {
                if (isMixed) {
                    name = simpleMixed;
                } else {
                    name = simplePhrases;
                }
            } else {
                name = simple;
            }
        }

        Path path = Paths.get(name);
        Map<String, Integer> ret = new HashMap<>();
        InputStream is = null;
        if (Files.isRegularFile(path)) {
            // laod file
            is = Files.newInputStream(path);
        } else {
            // try class Path
            is = SortFile.class.getClassLoader().getResourceAsStream(name);
            if (is == null) {
                return ret;
            }
        }
        try (InputStreamReader r = new InputStreamReader(is,
                StandardCharsets.UTF_8);
             BufferedReader reader = new BufferedReader(r)) {
            String s;
            int cnt = 0;
            while ((s = reader.readLine()) != null) {
                ret.put(s, cnt++);
            }
        } finally {
            try {
                is.close();
            } catch (IOException e) {

            }
        }

        return ret;
    }

}
