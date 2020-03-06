package me.asu.word.wmff;


import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.*;
import lombok.extern.slf4j.Slf4j;
import me.asu.util.Streams;
import me.asu.word.ResourcesFiles;
import me.asu.word.Word;
import me.asu.word.shortern.MergedMakeShort;

/**
 * Created by suk on 2019/6/2.
 */
@Slf4j
public class Wmff {
    public static void main(String[] args) throws IOException {

//        List<String> lines = ResourcesFiles.readLinesInResources("wmff.txt");
//        List<String> oneSet = ResourcesFiles.readLinesInResources("wmff-single-words.txt");
        List<String> lines = ResourcesFiles.readLinesInResources("wmpf.txt");
        List<String> oneSet = ResourcesFiles.readLinesInResources("wmpf-single.txt");

        Map<String, List<Word>> results = new TableMakeShort().makeSort(lines, oneSet);

        File outFile = new File("out","wmff-out.txt");
        List<Word> result = results.get("result");
        writeTo(result, outFile);
        System.out.println("保存到：" + outFile.getAbsolutePath());

        File outFullFile = new File("out","wmff-full.txt");
        List<Word> full = results.get("full");
        writeTo(full, outFullFile);
        System.out.println("保存到：" + outFullFile.getAbsolutePath());

        File outOtherFile = new File("out","wmff-other.txt");
        List<Word> sp = results.get("uncommon");
        writeTo(sp, outOtherFile);
        System.out.println("保存到：" + outOtherFile.getAbsolutePath());
    }

    private static void writeTo(List<Word> list, File outFile) throws IOException {
        try(Writer writer = Streams.fileOutw(outFile)) {
            Collections.sort(list);
            for (Word w : list) {
                writer.write(String.format("%s\t%s\t%s%n", w.getWord(), w.getCode(), w.getLevel()));
            }
        }
    }


//    // 定义命令行参数
//    private static Options definedOptions(){
//        Option opt_h = new Option("h", "help""打印本信息。");
//        Option opt_e = new Option("e", "encoding", true, "文件编码。");
//        Option opt_i = new Option("i", "input", true, "输入文件。");
////        Option opt_k = Option.builder("k").hasArg().argName("keyFile")
////                             .desc("Specify the key file").build();
////        Option opt_f = Option.builder("f").hasArgs().argName("file1,file2...")
////                             .valueSeparator(',')
////                             .desc("A files list with ',' separate to handle").build();
//        // 解析
//        //        if (line.hasOption("r")) {
//        //            String[] rs = line.getOptionValues("r");
//        //            for(String r : rs){
//        //                File dir = new File(r);
//        //                if(dir.isDirectory()){
//        //                    dirs.add(dir);
//        //                    DirUtil dirUtil = new DirUtil(dir);
//        //                    files.addAll(Arrays.asList(dirUtil.getFiles()));
//        //                    dirs.addAll(Arrays.asList(dirUtil.getDirs()));
//        //                }else{
//        //                    System.err.println(dir + " is not a directory");
//        //                    System.exit(1);
//        //                }
//        //            }
//        //        }
//        Options opts = new Options();
//        opts.addOption(opt_h);
//        opts.addOption(opt_e);
//        opts.addOption(opt_i);
//        return opts;
//    }
//    // 解析处理命令行参数
//    private static CommandLine parseOptions(String[] args){
//        CommandLineParser parser = new DefaultParser();
//        CommandLine line = null;
//        // 解析命令行参数
//        try {
//            return parser.parse(definedOptions(), args);
//        } catch (ParseException e) {
//            System.err.println(e.getMessage());
//            System.exit(1);
//        }
//    }
}
