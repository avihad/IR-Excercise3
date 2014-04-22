package program;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import searchengine.BasicSearchEngine;
import searchengine.ISearchEngine;
import utils.DocFactory;
import utils.Utilities;
import entities.EngineStrategy;
import entities.IRDoc;
import entities.SearchResult;

public class Main {

    public static void main(String[] args) {
	if (args == null || args.length < 1) {
	    System.out.println("Error: input should contain properties file location");
	    System.exit(1);
	}

	Main m = new Main(args[0]);
	m.Start();

    }

    private String	queryFilePath;
    private String	docsFilePath;
    private String	outputFilePath;
    private String	retriveAlgorithmPath;
    private String	propFilePath;
    private ISearchEngine luceneInstance;

    public Main(String propFilePath) {
	this.propFilePath = propFilePath;
    }

    private List<IRDoc> createDocuments(String path) {
	List<IRDoc> parsedDocs = new LinkedList<IRDoc>();

	Map<Integer, String> parsedLines = Utilities.simpleIRParser(path);

	for (Map.Entry<Integer, String> line : parsedLines.entrySet()) {
	    parsedDocs.add(DocFactory.instance.create(line.getKey(), line.getValue()));
	}

	return parsedDocs;
    }

    private void IndexDocuments() {

	List<IRDoc> documents = createDocuments(this.docsFilePath);

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
		EngineStrategy strategy = EngineStrategy.valueOf(this.retriveAlgorithmPath);
		DocFactory.instance.setStrategy(strategy);

		this.luceneInstance = BasicSearchEngine.createEngine(this.retriveAlgorithmPath);
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
	List<IRDoc> documents = createDocuments(this.queryFilePath);

	if (!documents.isEmpty()) {
	    File outputFile = new File(this.outputFilePath);

	    List<SearchResult> queryResults;
	    for (IRDoc doc : documents) {
		queryResults = this.luceneInstance.search(doc.getContent());
		Utilities.printSearchResults(outputFile, Integer.toString(doc.getId()), queryResults);
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
