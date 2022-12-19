package me.asu.startdict;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * magic data: "StarDict's treedict ifo file"
 * version: "2.4.2"
 * <p>
 * Available options:
 * <p>
 * bookname=      // required
 * wordcount=     // required
 * tdxfilesize=   // required
 * author=
 * email=
 * website=
 * description=	// You can use <br> for new line.
 * date=
 * sametypesequence=
 */
public class Info {

    final static String MAGIC_DATA      = "StarDict's treedict ifo file";
    final static String DEFAULT_VERSION = "2.4.2";
    String version = DEFAULT_VERSION;

    String                 bookname;      // required
    int                 wordcount;     // required
    long                 tdxfilesize;   // required
    String                 author;
    String                 email;
    String                 website;
    String                 description;    // You can use <br> for new line.
    String                 date;
    Map<SameType, Integer> sametypeMap = new HashMap<>(); //sametypesequence

    public boolean isNotSettingSameType() {
        return sametypeMap == null || sametypeMap.isEmpty();
    }

    public int getSamTypeIndex (SameType t) {
        if (isNotSettingSameType()) return -1;
        Integer integer = sametypeMap.get(t);
        if (integer == null) return -1;
        return integer;
    }

    public void load(Path path) throws IOException {
        List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
        if (lines == null || lines.isEmpty()) {
            throw new IOException("No content.");
        }
        String md = lines.get(0);
        if (MAGIC_DATA.equals(md)) { throw new IOException("Support Format"); }
        for (int j = 1, linesSize = lines.size(); j < linesSize; j++) {
            String line = lines.get(j);
            if (line == null || line.trim().isEmpty()) continue;
            String[] kv = line.split("=");

            switch (kv[0]) {
                case "bookname":
                    this.bookname = kv[1];
                    break;
                case "wordcount":
                    this.wordcount =  kv[1] == null ? 0 : Integer.parseInt(kv[1]);
                    break;
                case "idxfilesize":
                    this.tdxfilesize = kv[1] == null ? 0 : Long.parseLong(kv[1]);
                    break;
                case "author":
                    this.author =  kv[1];
                    break;
                case "email":
                    this.email =  kv[1];
                    break;
                case "website":
                    this.website =  kv[1];
                    break;
                case "description":
                    this.description =  kv[1];
                    break;
                case "date":
                    this.date =  kv[1];
                    break;
                case "sametypesequence":
                    char[] split =  kv[1].toCharArray();
                    for (int i = 0; i < split.length; i++) {
                        String c = ""+split[i];
                        SameType s = SameType.getByCode(c);
                        if (s != null) {
                            sametypeMap.put(s, i);
                        }
                    }

                    break;
            }
        }
    }

    @Override
    public String toString() {
        return "Info{" +
                "version='" + version + '\'' +
                ", bookname='" + bookname + '\'' +
                ", wordcount='" + wordcount + '\'' +
                ", tdxfilesize='" + tdxfilesize + '\'' +
                ", author='" + author + '\'' +
                ", email='" + email + '\'' +
                ", website='" + website + '\'' +
                ", description='" + description + '\'' +
                ", date='" + date + '\'' +
                ", sametypeMap=" + sametypeMap +
                '}';
    }
}
