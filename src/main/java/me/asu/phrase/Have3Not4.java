package me.asu.phrase;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import me.asu.word.ResourcesFiles;

/**
 * Created by gw-meet1-0 on 2020/3/6.
 */
public class Have3Not4
{

	public static void main(String[] args) throws IOException
	{
		Map<String, String> map3 = ResourcesFiles.loadAsMap("3-1.txt");
		Map<String, String> map4 = ResourcesFiles.loadAsMap("he_phrases-4.txt");

		File outDir = new File("out");
		if (!outDir.exists()) {
			outDir.mkdirs();
		}
		File out1 = new File("out", "a.txt");
		File out2 = new File("out", "b.txt");
		FileWriter fw1 = new FileWriter(out1);
		FileWriter fw2 = new FileWriter(out2);

		map3.forEach((k, v) -> {
			if (map4.containsKey(k)) {
				try {
					fw1.write(String.format("%s\t%s\n",k, v));
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				try {
					fw2.write(String.format("%s\t%s\n",k, v));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});

		fw1.close();
		fw2.close();

		System.out.println("Done!");
	}

}
