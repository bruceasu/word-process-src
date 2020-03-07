package me.asu.phrase;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import me.asu.word.ResourcesFiles;

/**
 * @author suk
 */
public class CleanupPhrases
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

		File       out1 = new File("out", "c.txt");
		File       out2 = new File("out", "d.txt");
		FileWriter fw1  = new FileWriter(out1);
		FileWriter fw2  = new FileWriter(out2);

		List<String> nouns    = ResourcesFiles.readLinesInResources("名词.txt");
		Set<String>  nounsSet = new HashSet<>();
		nounsSet.addAll(nouns);

		cleanupPhrases3(codeSet, phraseCodeSet, nounsSet, fw1, fw2);

		List<String> phrases4 = ResourcesFiles.readLinesInResources("phrases-4.txt");
		phrases4.forEach(line -> {
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
				if ((freqWords.contains(c1) && freqWords.contains(c2))
						|| (singleWords.contains(c1) || singleWords.contains(c2))) {
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

		fw1.close();
		fw2.close();
		System.out.println("Done!");
	}

	private static void cleanupPhrases3(Set<String> codeSet,
	                                    Set<String> phraseCodeSet,
	                                    Set<String> nounsSet,
	                                    FileWriter fw1,
	                                    FileWriter fw2)
	{
		List<String> phrases3 = ResourcesFiles.readLinesInResources("phrases-3.txt");
		phrases3.forEach(line -> {
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
		if (isDup && !isNoun) {
			// 重 且 不是专有名词
			try {
				fw1.write(String.format("%s\t%s\n", kv[0], kv[1]));
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			if (phraseCodeSet.contains(kv[1])) {
				// 重
				try {
					fw1.write(String.format("%s\t%s\n", kv[0], kv[1]));
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				//phraseCodeSet.add(kv[1]);
				try {
					fw2.write(String.format("%s\t%s\n", kv[0], kv[1]));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}
	}
}
