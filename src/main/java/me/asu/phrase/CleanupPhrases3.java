package me.asu.phrase;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import me.asu.word.ResourcesFiles;

/**
 * @author suk
 */
public class CleanupPhrases3
{

	public static void main(String[] args) throws IOException
	{

		// 精简无重
		File       out1 = new File("out", "a.txt");
		FileWriter fw1  = new FileWriter(out1);

		List<String> nouns    = ResourcesFiles.readLinesInResources("名词.txt");
		Set<String>  nounsSet = new HashSet<>();
		nounsSet.addAll(nouns);

		Set<String> cp = new HashSet<>(ResourcesFiles.readLinesInResources("核心词库-he.txt", "UTF-16"));
		
		List<String> list = ResourcesFiles.readLinesInResources("系统词库57000.txt");
		List<String> L1 = new ArrayList<>();
		List<String> L2 = new ArrayList<>();
		list.forEach(s->{
			if (s == null || s.trim().isEmpty()) {
				return;
			}
			String[] kv = s.trim().split("\\s+");
			if (cp.contains(s)) {
				L1.add(s.trim());
			} else {
				L2.add(s.trim());
			}
		});
		


		cleanupPhrases(L1, L2,  nounsSet, fw1);

		fw1.close();
		System.out.println("Done!");
	}

	private static void cleanupPhrases(List<String> commons,
	                                   List<String> phrases,
	                                   Set<String> nounsSet,
	                                   FileWriter fw) throws IOException {
		HashMap<String, AtomicInteger> exists = new HashMap<>();
		commons.forEach(line->{
			String[] split = line.split("\\s+");
			if (split.length == 2) {
				addExists(exists, split[1]);
			}
		});
		for (String line : phrases) {
			if (line.trim().isEmpty()) {
				continue;
			}

			String[] kv     = line.trim().split("\\s+");
			boolean  isNoun = nounsSet.contains(kv[0]);

			if (isNoun) {
				// output
				fw.write(String.format("%s\t%s%n", kv[0], kv[1]));
				addExists( exists, kv[1]);
			} else if (!exists.containsKey(kv[1])) {
				// output
				fw.write(String.format("%s\t%s%n", kv[0], kv[1]));
				addExists( exists, kv[1]);
			} else if (exists.get(kv[1]).get() < 2) {
				// output
				fw.write(String.format("%s\t%s%n", kv[0], kv[1]));
				addExists( exists, kv[1]);
			} else {
				// ignore
			}


		}
	}

	private static void addExists(HashMap<String, AtomicInteger> exists, String split) {
		if (exists.containsKey(split)) {
			exists.get(split).incrementAndGet();
		} else {
			exists.put(split, new AtomicInteger(1));
		}
	}


}
