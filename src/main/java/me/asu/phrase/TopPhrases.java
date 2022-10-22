package me.asu.phrase;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import me.asu.word.ResourcesFiles;

/**
 * @author suk
 */
public class TopPhrases {

    public static void main(String[] args) throws IOException {
	    File outDir = new File("out");
	    if (!outDir.exists()) {
		    outDir.mkdirs();
	    }
	    File in = new File("out", "d2.txt");
	    File out = new File("out", "d1.txt");
	    System.out.println("Reading " + in);
	    int top = 5;
	    try (BufferedReader bufferedReader = Files
			    .newBufferedReader(in.toPath());
	         BufferedWriter writer = Files.newBufferedWriter(out.toPath(),
			         StandardCharsets.UTF_8)) {
		    String line;
		    Map<String, AtomicInteger> map = new HashMap<>();
		    while ((line = bufferedReader.readLine()) != null) {
			    if (line.startsWith("#"))
				    continue;
			    String[] split = line.split("\\s+");
			    if (split.length < 2)
				    continue;
			    map.putIfAbsent(split[1], new AtomicInteger(0));
			    AtomicInteger cnt = map.get(split[1]);
			    if (cnt.get() >= top)
				    continue;
			    writer.write(line);
			    writer.newLine();
			    cnt.incrementAndGet();
		    }
	    }
	    System.out.println("Done!");
	    System.out.println("Save to " + out);
    }
}
