package me.asu.word.hyly;


import static me.asu.word.ResourcesFiles.loadAsMapList;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import me.asu.util.Streams;
import me.asu.word.Merge;
import me.asu.word.ResourcesFiles;
import me.asu.word.Word;
import me.asu.word.shortern.MergedMakeShort2;

/**
 * 主打单字
 */
public class Hyly2
{

	public static void main(String[] args) throws IOException
	{
		String                    name   = "he";
		Map<String, List<String>> he     = loadAsMapList("he.txt");
		List<String>              oneSet = ResourcesFiles.readLinesInResources("he_1_2.txt");
		List<String>              twoSet = ResourcesFiles.readLinesInResources("phrases-2.txt");
		oneSet.addAll(twoSet);
		Map<String, List<String>> xm = loadAsMapList("kuaishou.less.txt");
		List<Word> merged = Merge.merge(he, xm);
		Map<String, List<Word>> results = new MergedMakeShort2().makeSort(merged, oneSet);

		save(name, results);
	}

	protected static void save(String name, Map<String, List<Word>> results) throws IOException
	{
		File outdir=  new File("out");
		if (!outdir.exists()) {
			outdir.mkdirs();
		}
		File       outFile = new File(outdir, name + "-out.txt");
		List<Word> result  = results.get("result");
		writeTo(result, outFile, 60000);
		System.out.println("保存到：" + outFile.getAbsolutePath());

		File       outFile2 = new File(outdir, name + "-out2.txt");
		List<Word> result2  = results.get("result2");
		writeTo(result2, outFile2, 30000);
		System.out.println("保存到：" + outFile2.getAbsolutePath());

		File       outFullFile = new File(outdir, name + "-full.txt");
		List<Word> full        = results.get("full");
		writeFullTo(full, outFullFile, 20000);
		System.out.println("保存到：" + outFullFile.getAbsolutePath());

		File       outOtherFile = new File(outdir, name + "-other.txt");
		List<Word> sp           = results.get("uncommon");
		writeSpTo(sp, outOtherFile, 10000);
		System.out.println("保存到：" + outOtherFile.getAbsolutePath());
	}

	protected static void writeTo(List<Word> list, File outFile, int startFreq) throws IOException
	{
		try (Writer writer = Streams.fileOutw(outFile)) {
			Collections.sort(list);
			for (int i = 0; i < list.size(); i++) {
				Word   w    = list.get(i);
				int    freq = startFreq - i;
				String ext;
				ext = String.format("#序%d", freq);
				writer.write(String.format("%s\t%s%s%n", w.getWord(), w.getCode(), ext));
			}
		}
	}

	protected static void writeFullTo(List<Word> list, File outFile, int startFreq)
	throws IOException
	{
		try (Writer writer = Streams.fileOutw(outFile)) {
			Collections.sort(list);
			for (int i = 0; i < list.size(); i++) {
				Word   w    = list.get(i);
				int    freq = startFreq - i;
				String ext  = String.format("#序%d", freq);
				writer.write(String.format("%s\t%s%s%n", w.getWord(), w.getCode(), ext));
			}
		}
	}

	protected static void writeSpTo(List<Word> list, File outFile, int startFreq) throws IOException
	{
		try (Writer writer = Streams.fileOutw(outFile)) {
			Collections.sort(list);
			for (int i = 0; i < list.size(); i++) {
				Word   w    = list.get(i);
				int    freq = startFreq - i;
				String ext  = String.format("#序%d", freq);
				writer.write(String.format("%s\t%s%s%n", w.getWord(), w.getCode(), ext));
			}
		}
	}
}
