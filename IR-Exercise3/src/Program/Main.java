package Program;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import LuceneWrapper.BaseLuceneWrapper;
import LuceneWrapper.ILuceneWrapper;
import LuceneWrapper.MyDoc;
import LuceneWrapper.SearchResult;

public class Main {

    public static void main(String[] args) {
	if (args == null || args.length < 1) {
	    System.out.println("Error: input should contain properties file location");
	    System.exit(1);
	}

	Main m = new Main(args[0]);
	m.Start();

    }

    private String	 queryFilePath;
    private String	 docsFilePath;
    private String	 outputFilePath;
    private String	 retriveAlgorithmPath;
    private String	 propFilePath;
    private ILuceneWrapper luceneInstance = null;

    public Main(String propFilePath) {
	this.propFilePath = propFilePath;
    }

    private List<MyDoc> documentsParser(String path) {
	List<MyDoc> parsedDocs = new LinkedList<MyDoc>();

	List<String> lines = Utils.ReadLinesFromFile(path);

	MyDoc tempDoc;

	for (String line : lines) {
	    tempDoc = Utils.GetMyDocFromStr(line);

	    if (tempDoc != null) {
		parsedDocs.add(tempDoc);
	    }
	}

	return parsedDocs;
    }

    private void IndexDocuments() {

	List<MyDoc> documents = documentsParser(this.docsFilePath);

	if (documents.isEmpty()) {
	    System.out.println("Error: no documents were read from documents file");
	    return;
	}

	if (this.luceneInstance.index(documents)) {
	    System.out.println("Info: successfully indexed all documents");
	}

    }

    private boolean InitVariables() {

	boolean success = false;
	Properties prop = new Properties();
	InputStream input = null;
	try {
	    System.out.println("Info: Attempting to read input file. path=" + this.propFilePath);
	    input = new FileInputStream(this.propFilePath);
	    prop.load(input);

	    // get the property value and print it out
	    this.queryFilePath = prop.getProperty("queryFile");
	    this.docsFilePath = prop.getProperty("docsFile");
	    this.outputFilePath = prop.getProperty("outputFile");
	    this.retriveAlgorithmPath = prop.getProperty("retrievalAlgorithm");

	    if (this.queryFilePath == null || this.docsFilePath == null || this.outputFilePath == null
		    || this.retriveAlgorithmPath == null) {
		System.out.println("Error: properties file is missing parameters");
	    } else {
		this.luceneInstance = BaseLuceneWrapper.GetInstance(this.retriveAlgorithmPath);
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

    private void Search() {
	List<MyDoc> documents = documentsParser(this.queryFilePath);

	if (!documents.isEmpty()) {
	    File outputFile = new File(this.outputFilePath);

	    List<SearchResult> queryResults;
	    for (MyDoc doc : documents) {
		queryResults = this.luceneInstance.search(doc.getContent());
		Utils.PrintSearchResults(outputFile, Integer.toString(doc.getId()), queryResults);
	    }
	} else {
	    System.out.println("Error: no documents were read from query file");
	}
    }

    public void Start() {

	if (!InitVariables()) {
	    System.out.println("Error: initiating variables from properties file failed");
	    System.exit(2);
	}

	// read documents from file and build index
	IndexDocuments();

	// read queries from file and search against index
	Search();
    }

}
