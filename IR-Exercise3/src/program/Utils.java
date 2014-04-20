package program;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import entities.SearchResult;
import entities.SimpleIRDoc;

public class Utils {

    public static SimpleIRDoc getMyDocFromStr(String str) {
	SimpleIRDoc doc = null;
	if (str != null && !str.isEmpty()) {
	    String trimmedStr = str.trim();
	    int firstWS = trimmedStr.indexOf(' ');

	    // check that line contains a doc id (first word in doc) and a non-empty content
	    if (firstWS > -1 && trimmedStr.length() > (firstWS + 1)) {
		try {
		    String docId = trimmedStr.substring(0, firstWS);
		    int nDocId = Integer.parseInt(docId);

		    String content = trimmedStr.substring(firstWS + 1);

		    doc = new SimpleIRDoc(nDocId, content);

		} catch (NumberFormatException nfe) {
		    nfe.printStackTrace();
		}

	    }
	}

	return doc;
    }

    private static String getOutputRow(String qID, String docID, float score) {
	StringBuilder sb = new StringBuilder();
	sb.append("q");
	sb.append(qID);
	sb.append(",");

	if (docID == null) {
	    sb.append("dummy");
	} else {
	    sb.append("doc");
	    sb.append(docID);
	}
	sb.append(",");
	sb.append(String.format("%.1f", score));
	sb.append("\n");

	return sb.toString();

    }

    public static void printSearchResults(File file, String queryID, List<SearchResult> results) {

	BufferedWriter writer = null;
	try {

	    if (!file.exists()) {
		file.createNewFile();
		System.out.println("Info: Create new file for output: " + file.getAbsolutePath());
	    }

	    if (!file.canWrite()) {
		System.out.println("Error: Cannot open output file for writing. Filepath="
			+ file.getAbsolutePath());
		return;
	    }

	    writer = new BufferedWriter(new FileWriter(file, true));

	    List<String> rows = new LinkedList<String>();
	    if (results == null || results.isEmpty()) {
		rows.add(getOutputRow(queryID, null, 0));
	    } else {
		for (SearchResult result : results) {
		    rows.add(getOutputRow(queryID, result.getDocId(), result.getScore()));
		}
	    }
	    for (String row : rows) {
		writer.write(row);
	    }
	} catch (IOException e1) {
	    e1.printStackTrace();

	} finally {
	    if (writer != null) {
		try {
		    writer.close();
		} catch (IOException e) {
		    e.printStackTrace();
		}
	    }
	}

    }

    public static List<String> readLinesFromFile(String filePath) {
	List<String> lines = new LinkedList<String>();

	File file = new File(filePath);
	BufferedReader br = null;
	try {
	    br = new BufferedReader(new FileReader(file));
	    String line;

	    while ((line = br.readLine()) != null) {
		lines.add(line);
	    }
	} catch (IOException e) {
	    e.printStackTrace();
	} finally {
	    if (br != null) {
		try {
		    br.close();
		} catch (IOException e) {
		    e.printStackTrace();
		}
	    }
	}

	return lines;
    }

    /**
     * Reads a file structured as number and content and parsed it to a map
     * 
     * @param filePath
     *            path to the file need to be parsed
     * */
    public static Map<Integer, String> simpleIRParser(String filePath) {
	Map<Integer, String> results = new HashMap<Integer, String>();
	List<String> lines = Utils.readLinesFromFile(filePath);

	for (String line : lines) {
	    String trimmedStr = line.trim();
	    int firstWhiteSpace = trimmedStr.indexOf(' ');

	    // check that line contains an id and a non-empty content
	    if (firstWhiteSpace > -1 && trimmedStr.length() > (firstWhiteSpace + 1)) {
		try {
		    String idString = trimmedStr.substring(0, firstWhiteSpace);
		    int id = Integer.parseInt(idString);

		    String content = trimmedStr.substring(firstWhiteSpace + 1);

		    results.put(id, content);

		} catch (NumberFormatException nfe) {
		    nfe.printStackTrace();
		}

	    }

	}
	return results;
    }
}
