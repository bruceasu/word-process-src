package me.asu.word.hyly;


import static me.asu.word.ResourcesFiles.loadAsMapList;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.util.*;
import me.asu.util.Streams;
import me.asu.word.ResourcesFiles;
import me.asu.word.Word;

/**
 * 前500无法正常编码的用无理编码。
 */
public class FindUnUseCode
{

	public static void main(String[] args) throws IOException
	{
		Map<String, List<String>> oneSet = loadAsMapList("he_1_2.txt");
		List<String> the500 = ResourcesFiles.readLinesInResources("common-words-500.txt");
		File outdir = new File("out");
		if (!outdir.exists()) {
			outdir.mkdirs();
		}
		File outFile = new File(outdir, "no-code-out.txt");
		try (BufferedWriter writer = Files.newBufferedWriter(outFile.toPath())) {
			the500.forEach(z -> {
				if (!oneSet.containsKey(z)) {
					try {
						writer.write(z);
						writer.write(System.lineSeparator());
					} catch (IOException e) {
						e.printStackTrace();
					}

				}
			});
		}
		HashSet<String> codes = new HashSet<>();
		oneSet.values().forEach(codes::addAll);
		// make available
		List<String> a = new ArrayList<>(26 * 26);
		for (char i = 'a'; i < 'z'; i++) {
			for (char j = 'a'; j < 'z'; j++) {
				a.add(new String(new char[]{i, j}));
			}
		}
		File outFile2 = new File(outdir, "no-word-out.txt");
		try (BufferedWriter writer = Files.newBufferedWriter(outFile2.toPath())) {
			a.forEach(z -> {
				if (!codes.contains(z)) {
					try {
						writer.write(z);
						writer.write(System.lineSeparator());
					} catch (IOException e) {
						e.printStackTrace();
					}

				}
			});
		}

		System.out.println("DONE.");
	}

	protected static void save(String name, Map<String, List<Word>> results) throws IOException
	{
		File outdir = new File("out");
		if (!outdir.exists()) {
			outdir.mkdirs();
		}
		File outFile = new File(outdir, name + "-out.txt");
		List<Word> result = results.get("result");
		writeTo(result, outFile, 60000);
		System.out.println("保存到：" + outFile.getAbsolutePath());

		File outFile2 = new File(outdir, name + "-out2.txt");
		List<Word> result2 = results.get("result2");
		writeTo(result2, outFile2, 20000);
		System.out.println("保存到：" + outFile2.getAbsolutePath());

		File outFullFile = new File(outdir, name + "-full.txt");
		List<Word> full = results.get("full");
		writeFullTo(full, outFullFile, 15000);
		System.out.println("保存到：" + outFullFile.getAbsolutePath());

		File outOtherFile = new File(outdir, name + "-other.txt");
		List<Word> sp = results.get("uncommon");
		writeSpTo(sp, outOtherFile, 10000);
		System.out.println("保存到：" + outOtherFile.getAbsolutePath());
	}

	protected static void writeTo(List<Word> list, File outFile, int startFreq) throws IOException
	{
		try (Writer writer = Streams.fileOutw(outFile)) {
			Collections.sort(list);
			for (int i = 0; i < list.size(); i++) {
				Word w = list.get(i);
				int freq = startFreq - i;
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
				Word w = list.get(i);
				int freq = startFreq - i;
				String ext = String.format("#序%d", freq);
				writer.write(String.format("%s\t%s%s%n", w.getWord(), w.getCode(), ext));
			}
		}
	}

	protected static void writeSpTo(List<Word> list, File outFile, int startFreq) throws IOException
	{
		try (Writer writer = Streams.fileOutw(outFile)) {
			Collections.sort(list);
			for (int i = 0; i < list.size(); i++) {
				Word w = list.get(i);
				int freq = startFreq - i;
				String ext = String.format("#序%d", freq);
				writer.write(String.format("%s\t%s%s%n", w.getWord(), w.getCode(), ext));
			}
		}
	}
}
