package Program;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;

import LuceneWrapper.MyDoc;
import LuceneWrapper.SearchResult;

public class Utils {

	public static MyDoc GetMyDocFromStr(String str)
	{
		MyDoc doc = null;
		if(str != null && !str.isEmpty())
		{
			String trimmedStr = str.trim();
			int firstWS = trimmedStr.indexOf(' ');
			
			//check that line contains a doc id (first word in doc) and a non-empty content
			if(firstWS > -1 && trimmedStr.length() > (firstWS + 1) )
			{
				try
				{
				String docId = trimmedStr.substring(0, firstWS);
				int nDocId = Integer.parseInt(docId);
				
				String content = trimmedStr.substring(firstWS + 1);
				
				doc = new MyDoc(nDocId, content);
				
				}catch(NumberFormatException nfe)
				{
					nfe.printStackTrace();
				}
				
			}
		}
		
		return doc;
	}
	
	public static String[] ReadLinesFromFile(File file) throws IOException
	{
		LinkedList<String> lines = new LinkedList<String>();
		
		if(file.canRead())
		{
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line;
			
			while((line = br.readLine()) != null)
			{
				lines.add(line);
			}
			
			br.close();
		}
		else
		{
			throw new FileNotFoundException("Error: file cannot be read. Path=" + file.getAbsolutePath());
		}
		
		return lines.toArray(new String[lines.size()]);
	}
	
	public static void PrintSearchResults(File file, String queryID, SearchResult[] results)
	{
		if(file.canWrite())
		{
			BufferedWriter writer = null;
			try {
				writer = new BufferedWriter(new FileWriter(file));
	
			LinkedList<String> rows = new LinkedList<String>();
			if(results == null || results.length == 0)
			{
				rows.add(getOutputRow(queryID, null, 0));
			}
			else
			{
				for(SearchResult result : results)
				{
					rows.add(getOutputRow(queryID, result._docId, result._score));
				}
			}
				for (String row : rows) {
					writer.write(row);
				}
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();

			} finally {
				if (writer != null) {
					try {
						writer.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
		else
		{
			System.out.println("Error: Cannot open output file for writing. Filepath=" + file.getAbsolutePath());
		}
	}
	
	private static String getOutputRow(String qID, String docID, float score)
	{
		StringBuilder sb = new StringBuilder();
		sb.append("q");
		sb.append(qID);
		sb.append(",");
		
		if(docID == null)
			sb.append("dummy");
		else
		{
			sb.append("doc");
			sb.append(docID);
		}
		sb.append(",");
		sb.append(String.format("%.1f", score));
		sb.append("\n");
		
		return sb.toString();
		
	}
}
