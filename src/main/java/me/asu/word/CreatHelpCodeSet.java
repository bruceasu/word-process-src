package me.asu.word;

import static me.asu.word.ResourcesFiles.toMapList;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import me.asu.util.io.Files;
import me.asu.util.io.Streams;
import org.apache.commons.cli.*;

/**
 * Created by suk on 2019/6/8.
 */
public class CreatHelpCodeSet {
    // 定义命令行参数
    private static Options definedOptions(){
        Option opt_h = new Option("h", "help",false,"打印本信息。");
        Option opt_e = new Option("e", "encoding", true, "文件编码。");
        Option opt_i = new Option("i", "input", true, "输入文件。");
        Option opt_m = new Option("m", "mapping", true, "比对文件。");
        Option opt_o = Option.builder("o").hasArg().argName("output").desc("输出文件").build();
        //        Option opt_f = Option.builder("f").hasArgs().argName("file1,file2...")
//                             .valueSeparator(',')
//                             .desc("A files list with ',' separate to handle").build();
        // 解析
        //        if (line.hasOption("r")) {
        //            String[] rs = line.getOptionValues("r");
        //            for(String r : rs){
        //                File dir = new File(r);
        //                if(dir.isDirectory()){
        //                    dirs.add(dir);
        //                    DirUtil dirUtil = new DirUtil(dir);
        //                    files.addAll(Arrays.asList(dirUtil.getFiles()));
        //                    dirs.addAll(Arrays.asList(dirUtil.getDirs()));
        //                }else{
        //                    System.err.println(dir + " is not a directory");
        //                    System.exit(1);
        //                }
        //            }
        //        }
        Options opts = new Options();
        opts.addOption(opt_h);
        opts.addOption(opt_e);
        opts.addOption(opt_i);
        opts.addOption(opt_m);
        opts.addOption(opt_o);
        return opts;
    }
    // 解析处理命令行参数
    private static CommandLine parseOptions(String[] args){
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

    public static void main(String[] args) {
        CommandLine commandLine = parseOptions(args);
        String file_i = null, file_m = null, file_o, file_e = "UTF-8";
        if (commandLine.hasOption('h')) {
            printUsage();
            System.exit(1);
        }
        if (commandLine.hasOption('i')) {
            file_i = commandLine.getOptionValue('i');
        } else {
            printUsage();
            System.exit(1);
        }
        if (commandLine.hasOption('m')) {
            file_m = commandLine.getOptionValue('m');
        } else {
            printUsage();
            System.exit(1);
        }
        if (commandLine.hasOption('o')) {
            file_o = commandLine.getOptionValue('o');
        } else {
            file_o= file_i + ".out";
        }
        if (commandLine.hasOption('e')) {
            file_e = commandLine.getOptionValue('e');
        }
        exec(file_i, file_m, file_o, file_e);
    }

    public static void exec(String file_i, String file_m, String file_o, String file_e) {
        List<String> syllables = Files.readLines(new File(file_i), file_e);
        Map<String, List<String>> mapping =  toMapList(Files.readLines(new File(file_m), file_e));
        try(BufferedWriter buffw = Streams.buffw(Streams.fileOutw(file_o))) {
            syllables.forEach(line -> {
                if (line == null || line.trim().isEmpty()) return;
                String[] kv = line.split("\\s+");
                if (mapping.containsKey(kv[0])) {
                    mapping.get(kv[0]).forEach( code -> {
                        try {
                            buffw.write(String.format("$ddcmd(%s,%s[%s])\t%s%n", kv[0], kv[0], kv[1], code));
                            buffw.write(String.format("%s\t%s%n", kv[0], kv[1]));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                }
            });
            System.out.println("处里完成。");
            System.out.println("保存到" + file_o);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void printUsage() {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp( "CreateHelpCodeSet",  definedOptions(), true );
    }
}
