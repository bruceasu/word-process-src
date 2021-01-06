package me.asu.phrase;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import me.asu.word.ResourcesFiles;

/**
 * @author suk
 */
public class CleanupPhrases2
{

	public static void main(String[] args) throws IOException
	{
		Set<String>  w500          = ResourcesFiles.w500();
		Set<String>  codeSet       = new HashSet<>();
		Set<String>  phraseCodeSet = new HashSet<>();
		Set<String>  freqWords     = new HashSet<>();
		Set<String>  singleWords   = new HashSet<>();
		List<String> words         = ResourcesFiles.readLinesInResources("single.txt");
		words.forEach(line -> {
			if (line.trim().isEmpty()) {
				return;
			}
			String[] kv = line.trim().split("\\s+");
			codeSet.add(kv[1]);
			int     lenWord              = kv[0].length();
			int     lenCode              = kv[1].length();
			boolean isLen2               = lenCode == 2;
			boolean isIn500              = w500.contains(kv[0]);
			boolean isSingleWordWithLen2 = isLen2 && lenWord == 1;
			if (isIn500 || isSingleWordWithLen2) {
				freqWords.add(kv[0]);
			}
			if (lenWord == 1) {
				singleWords.add(kv[0]);
			}
		});
		// 精简无重
		File       out1 = new File("out", "a.txt");
		// 精简有重
		File       out2 = new File("out", "b.txt");
		// 其他无重
		File       out3 = new File("out", "c.txt");
		// 其他有重
		File       out4 = new File("out", "d.txt");
		FileWriter fw1  = new FileWriter(out1);
		FileWriter fw2  = new FileWriter(out2);
		FileWriter fw3  = new FileWriter(out3);
		FileWriter fw4  = new FileWriter(out4);
		
		List<String> nouns    = ResourcesFiles.readLinesInResources("名词.txt");
		Set<String>  nounsSet = new HashSet<>();
		nounsSet.addAll(nouns);

		Set<String> cp = new HashSet<>(ResourcesFiles.readLinesInResources("精简词汇.txt"));
		
		List<String> list = ResourcesFiles.readLinesInResources("系统词库57000.txt");
		List<String> L1 = new ArrayList<>();
		List<String> L2 = new ArrayList<>();
		list.forEach(s->{
			if (s == null || s.trim().isEmpty()) {
				return;
			}
			String[] kv = s.trim().split("\\s+");
			if (cp.contains(kv[0])) {
				L1.add(s.trim());
			} else {
				L2.add(s.trim());
			}
		});
		
		cleanupPhrases1(L1, codeSet, phraseCodeSet, nounsSet, fw1, fw2);

		cleanupPhrases2(L2, codeSet, phraseCodeSet, nounsSet, freqWords, singleWords, fw3, fw4);

		fw1.close();
		fw2.close();
		fw3.close();
		fw4.close();
		System.out.println("Done!");
	}

	private static void cleanupPhrases2(List<String> phrases, Set<String> codeSet, Set<String> phraseCodeSet, Set<String> nounsSet,
			Set<String> freqWords, Set<String> singleWords, FileWriter fw1, FileWriter fw2) {
		phrases.forEach(line -> {
			if (line.trim().isEmpty()) {
				return;
			}
			
			String[] kv      = line.trim().split("\\s+");
			boolean  isNoun  = nounsSet.contains(kv[0]);
			int      lenWord = kv[0].length();
			boolean  isDup   = codeSet.contains(kv[1]);
			String   c1      = kv[0].substring(0, 1);
			String   c2      = kv[0].substring(1, 2);
			if (lenWord == 2 && !isNoun) {
				if ((freqWords.contains(c1) && freqWords.contains(c2))) {
					// 高频字组成的词
					try {
						fw1.write(String.format("%s\t%s\n", kv[0], kv[1]));
					} catch (IOException e) {
						e.printStackTrace();
					}
					return;

				}
			}

			outputWithRule(fw1, fw2, kv, isNoun, isDup, phraseCodeSet);
		});
	}

	private static void cleanupPhrases1(List<String> phrases, 
										Set<String> codeSet,
	                                    Set<String> phraseCodeSet,
	                                    Set<String> nounsSet,
	                                    FileWriter fw1,
	                                    FileWriter fw2)
	{
		phrases.forEach(line -> {
			if (line.trim().isEmpty()) {
				return;
			}
			String[] kv     = line.trim().split("\\s+");
			boolean  isNoun = nounsSet.contains(kv[0]);
			boolean  isDup  = codeSet.contains(kv[1]);
			outputWithRule(fw1, fw2, kv, isNoun, isDup, phraseCodeSet);
		});
	}

	private static void outputWithRule(FileWriter fw1,
	                                   FileWriter fw2,
	                                   String[] kv,
	                                   boolean isNoun,
	                                   boolean isDup, Set<String> phraseCodeSet)
	{
		if (isNoun) {
			phraseCodeSet.add(kv[1]);
			try {
				fw1.write(String.format("%s\t%s\n", kv[0], kv[1]));
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if (isDup) {
			// 与字重
			try {
				fw2.write(String.format("%s\t%s\n", kv[0], kv[1]));
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			if (phraseCodeSet.contains(kv[1])) {
				// 重码词
				try {
					fw2.write(String.format("%s\t%s\n", kv[0], kv[1]));
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				phraseCodeSet.add(kv[1]);
				try {
					fw1.write(String.format("%s\t%s\n", kv[0], kv[1]));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}
	}
}
