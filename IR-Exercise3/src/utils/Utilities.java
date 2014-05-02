package utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import entities.IRDoc;
import entities.QueryIdealResult;
import entities.SearchResult;

public class Utilities {

	public static final List<String> MONTHS = Arrays.asList("January",
			"February", "March", "April", "May", "July", "June", "August",
			"September", "October", "November", "December");

	public static final List<String> NOT_KEYWORDS = Arrays.asList("JB", "CACM");

	public static final List<String> REFERENCES = Arrays.asList("references",
			"reference", "ref", "ref.");

	/**
	 * extract all the dates from the content in the format "JB <DATE> AM\PM"
	 * */
	public static List<String> extractDates(String content) {

		List<String> dates = new ArrayList<String>();
		// FIXME: change to the correct length
		final int dateMaxLength = 30;
		int startIndex = content.indexOf("JB");
		int pmEndIndex = 0;
		int amEndIndex = 0;

		while (startIndex > -1 && pmEndIndex > -1) {
			pmEndIndex = content.indexOf("PM", startIndex + 1);
			if (pmEndIndex > -1 && (pmEndIndex - startIndex) <= dateMaxLength) {
				dates.add(content.substring(startIndex + 2, pmEndIndex - 1));
			} else {
				amEndIndex = content.indexOf("AM", startIndex + 1);
				if (amEndIndex > -1
						&& (amEndIndex - startIndex) <= dateMaxLength) {
					dates.add(content.substring(startIndex + 2, amEndIndex - 1));
				}

			}
			startIndex = content.indexOf("JB", startIndex + 1);
		}
		return dates;
	}

	public static List<String> extractKeywords(String content) {
		List<String> keywords = new ArrayList<String>();
		String[] tokens = content.split(" ");

		String tmp;
		for (String token : tokens) {
			tmp = token.toUpperCase();
			if (token.length() > 1 && token.equals(tmp)) {
				keywords.add(token);
			}
		}
		keywords.removeAll(Utilities.NOT_KEYWORDS);
		return keywords;
	}

	public static List<String> extractReferences(String content) {
		List<String> references = new ArrayList<String>();
		String[] tokens = content.split(" ");

		boolean refFound = false;

		for (String ref : references) {
			if (refFound) {
				break;
			}

			for (int i = 0; i < tokens.length; i++) {
				if (refFound) {
					if (isNumeric(tokens[i])) {
						references.add(tokens[i]);
					} else {
						break;
					}
				} else if (tokens[i].equalsIgnoreCase(ref)) {
					refFound = true;
				}
			}
		}
		return references;
	}

	public static String GenericJoinToStr(Collection<?> c, String join) {
		StringBuilder sb = new StringBuilder();

		if (c != null && !c.isEmpty()) {
			Iterator<?> iter = c.iterator();

			sb.append(iter.next());

			while (iter.hasNext()) {
				sb.append(join);
				sb.append(iter.next());
			}
		}

		return sb.toString();
	}

	public static IRDoc getMyDocFromStr(String str) {
		IRDoc doc = null;
		if (str != null && !str.isEmpty()) {
			String trimmedStr = str.trim();
			int firstWS = trimmedStr.indexOf(' ');

			// check that line contains a doc id (first word in doc) and a
			// non-empty content
			if (firstWS > -1 && trimmedStr.length() > (firstWS + 1)) {
				try {
					String docId = trimmedStr.substring(0, firstWS);
					int nDocId = Integer.parseInt(docId);

					String content = trimmedStr.substring(firstWS + 1);

					doc = DocFactory.instance.create(nDocId, content);

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
		sb.append(String.format("%.3f", score));
		sb.append("\n");

		return sb.toString();

	}

	/**
	 * Parsing the truth file for the computation of the precision of our
	 * algorithms
	 * 
	 * @param filePath
	 *            - the path to the truth fie
	 * */
	public static List<QueryIdealResult> parseTruthLists(String filePath) {
		Map<Integer, QueryIdealResult> truthMap = new HashMap<Integer, QueryIdealResult>();

		List<String> linesFromFile = readLinesFromFile(filePath);

		QueryIdealResult tmp;

		for (String line : linesFromFile) {
			Pair<Integer, String> idContent = parseSingleLine(line);
			Integer queryId = idContent.first;
			String content = idContent.second;
			String[] splitedContent = content.split(" ");
			int docId = Integer.parseInt(splitedContent[1]);
			int rank = Integer.parseInt(splitedContent[3]);

			if (truthMap.containsKey(queryId)) {
				tmp = truthMap.get(queryId);
			} else {
				tmp = new QueryIdealResult(queryId);
				truthMap.put(queryId, tmp);
			}
			tmp.addResult(docId, rank);

		}

		return new ArrayList<QueryIdealResult>(truthMap.values());

	}

	public static boolean isNumeric(String str) {
		boolean result = false;
		try {
			Integer.parseInt(str);
			result = true;
		} catch (Exception ex) {
		}

		return result;
	}

	private static Pair<Integer, String> parseSingleLine(String line) {

		String trimmedStr = line.trim();
		int firstWhiteSpace = trimmedStr.indexOf(' ');

		// check that line contains an id and a non-empty content
		if (firstWhiteSpace > -1 && trimmedStr.length() > (firstWhiteSpace + 1)) {
			try {
				String idString = trimmedStr.substring(0, firstWhiteSpace);
				int id = Integer.parseInt(idString);

				String content = trimmedStr.substring(firstWhiteSpace + 1);

				return Pair.of(id, content);

			} catch (NumberFormatException nfe) {
				nfe.printStackTrace();
				return null;
			}

		}
		return null;

	}

	public static void printSearchResults(File file, String queryID,
			List<SearchResult> results) {

		BufferedWriter writer = null;
		try {

			if (!file.exists()) {
				file.createNewFile();
				System.out.println("Info: Create new file for output: "
						+ file.getAbsolutePath());
			}

			if (!file.canWrite()) {
				System.out
						.println("Error: Cannot open output file for writing. Filepath="
								+ file.getAbsolutePath());
				return;
			}

			writer = new BufferedWriter(new FileWriter(file, true));

			List<String> rows = new LinkedList<String>();
			if (results == null || results.isEmpty()) {
				rows.add(getOutputRow(queryID, null, 0));
			} else {
				for (SearchResult result : results) {
					rows.add(getOutputRow(queryID, result.getDocId(),
							result.getScore()));
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
		List<String> lines = Utilities.readLinesFromFile(filePath);

		for (String line : lines) {
			Pair<Integer, String> parsedLine = parseSingleLine(line);
			if (parsedLine != null) {
				results.put(parsedLine.first, parsedLine.second);
			}

		}
		return results;
	}

	public static double PrecisionAtN(int n, List<Integer> calculatedResults, QueryIdealResult idealResults){
		
		List<Integer> calculatedNResults = (calculatedResults.size() > n) ? calculatedResults.subList(0, n) : calculatedResults;
		
		int intersection = 0;
		int size = (n > idealResults.numOfResults()) ? idealResults.numOfResults() : n;
				
		for(int i = 0; i < size; i++)
		{
			for(int j = 0; j < size; j++)
			{
				if(calculatedNResults.get(j) == idealResults.getDocByRank(i))
				{
					intersection++;
					break;
				}
			}
		}
		
    	double result = (1.0 * intersection) / size;
    	
    	return result;
    }
 
}