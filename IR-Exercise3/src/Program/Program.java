package Program;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.Properties;

import LuceneWrapper.*;

public class Program {

	public static void main(String[] args) {
		if (args == null || args.length < 1) {
			System.out.println("Error: input should contain properties file location");
			System.exit(1);
		}

	}

	String _queryFile, _docsFile, _outputFile, _retAlg, _propFilePath;
	ILuceneWrapper _luceneInstance = null;
	
	public Program(String propFilePath)
	{
		_propFilePath = propFilePath;
	}

	public void Start() {
		
		if(!InitVariables())
		{
			System.out.println("Error: initiating variables from properties file failed");
			System.exit(2);
		}
		
		//read documents from file and build index 
		IndexDocuments();
		
		//read queries from file and search against index
		 Search();		
	}


	private void Search() {		
		MyDoc[] documents = ReadDocumentsFromFile(_queryFile);

		if (documents != null && documents.length > 0) {
			File outputFile = new File(_outputFile);
			
			SearchResult[] queryResults;
			for (MyDoc doc : documents) {
				queryResults = _luceneInstance.Search(doc._content);
				Utils.PrintSearchResults(outputFile, Integer.toString(doc._docId), queryResults);
			}
		}
		else
		{
			System.out.println("Error: no documents were read from query file");
		}
	}

	private void IndexDocuments() {
		
		MyDoc[] documents = ReadDocumentsFromFile(_docsFile);

		if (documents != null && documents.length > 0) 
		{
			Integer[] successfulDocs = _luceneInstance.Index(documents);

			if (successfulDocs != null && successfulDocs.length == documents.length) 
				System.out.println("Info: successfully indexed all documents");
		} 
		else
		{
			System.out.println("Error: no documents were read from documents file");
		}

	}

	private MyDoc[] ReadDocumentsFromFile(String path) {
		MyDoc[] result = null;
		
		File file = new File(path);
		
		try {
			String[] lines = Utils.ReadLinesFromFile(file);
			
			if(lines != null && lines.length > 0)
			{
				LinkedList<MyDoc> docs = new LinkedList<MyDoc>();
				
				MyDoc tempDoc;
				
				for(String line : lines)
				{
					tempDoc = Utils.GetMyDocFromStr(line);
					
					if(tempDoc != null)
						docs.add(tempDoc);
				}
				
				if(docs.size() > 0)
					docs.toArray(new String[docs.size()]);
			}
			
		} catch (IOException e) {
			System.out.println("Error: Could not read document file");
			e.printStackTrace();
		}
		
		return result;
	}

	private boolean InitVariables() {
		
		boolean success = false;
		Properties prop = new Properties();
		InputStream input = null;
		try {
			System.out.println("Info: Attempting to read input file. path=" + _propFilePath);
			input = new FileInputStream(_propFilePath);
			prop.load(input);

			// get the property value and print it out
			_queryFile = prop.getProperty("queryFile");
			_docsFile = prop.getProperty("docsFile");
			_outputFile = prop.getProperty("outputFile");
			_retAlg = prop.getProperty("retrievalAlgorithm");

			if (_queryFile == null || _docsFile == null || _outputFile == null || _retAlg == null) 
			{
				System.out.println("Error: properties file is missing parameters");
			} 
			else 
			{
				_luceneInstance = BaseLuceneWrapper.GetInstance(_retAlg);
				success = true;
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return success;
	}

}
