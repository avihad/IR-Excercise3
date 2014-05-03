package program;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
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
import entities.QueryIdealResult;
import entities.SearchResult;
import entities.SearchResultComparator;

public class Main {

    private static final String DEFUALT_STOPLIST_PATH = "stoplist.txt";
    private static final String DEFUALT_TRUTH_PATH = "truth.txt";

    public static void main(String[] args) {
	if (args == null || args.length < 1) {
	    System.out.println("Error: input should contain properties file location");
	    System.exit(1);
	}

	Main m = new Main(args[0]);
	m.Start();

    }

    private String queryFilePath;
    private String docsFilePath;
    private String outputFilePath;
    private String retriveAlgorithmPath;
    private String propFilePath;
    private ISearchEngine luceneInstance;
    private String stopListFilePath;
    private List<String> stoplist;
    private Map<Integer, List<SearchResult>> queryToResultsMap;
    private String truthFilePath;

    public Main(String propFilePath) {
	this.propFilePath = propFilePath;
	this.queryToResultsMap = new HashMap<Integer, List<SearchResult>>();
    }

    private List<IRDoc> createDocuments(String path) {
	List<IRDoc> parsedDocs = new LinkedList<IRDoc>();

	Map<Integer, String> parsedLines = Utilities.simpleIRParser(path);

	for (Map.Entry<Integer, String> line : parsedLines.entrySet()) {
	    parsedDocs.add(DocFactory.instance.create(line.getKey(), line.getValue()));
	}

	return parsedDocs;
    }

    private void indexDocuments() {

	System.out.println("Info: Parsing documents from the input path: " + this.docsFilePath);
	List<IRDoc> documents = createDocuments(this.docsFilePath);

	if (documents.isEmpty()) {
	    System.out.println("Error: no documents were read from documents file");
	    return;
	}

	if (this.luceneInstance.index(documents)) {
	    System.out.println("Info: successfully indexed all documents");
	}

    }

    private boolean initVariables() {

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
	    this.truthFilePath = prop.getProperty("truthFile");
	    this.truthFilePath = (this.truthFilePath == null || this.truthFilePath.isEmpty()) ? DEFUALT_TRUTH_PATH
		    : this.truthFilePath;
	    this.stopListFilePath = prop.getProperty("stoplist");
	    this.stopListFilePath = (this.stopListFilePath == null || this.stopListFilePath.isEmpty()) ? DEFUALT_STOPLIST_PATH
		    : this.stopListFilePath;

	    if (this.queryFilePath == null || this.docsFilePath == null || this.outputFilePath == null
		    || this.retriveAlgorithmPath == null) {
		System.out.println("Error: properties file is missing parameters");
	    } else {
		// EngineStrategy strategy = EngineStrategy.valueOf(this.retriveAlgorithmPath);
		// DocFactory.instance.setStrategy(strategy);
		DocFactory.instance.setStrategy(EngineStrategy.Improved);

		this.luceneInstance = BasicSearchEngine.createEngine(this.retriveAlgorithmPath);
		this.luceneInstance.setStopwords(this.stoplist);
		success = true;
		System.out.println("Info: finish parsing the input file");
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

    private void search() {

	System.out.println("Info: Parsing the query's from file: " + this.queryFilePath);
	List<IRDoc> documents = createDocuments(this.queryFilePath);

	if (!documents.isEmpty()) {
	    File outputFile = new File(this.outputFilePath);

	    System.out.println("Info: Searching the querys in our search engine");
	    List<SearchResult> queryResults;
	    for (IRDoc doc : documents) {
		queryResults = this.luceneInstance.search(doc.getContent());
		Collections.sort(queryResults, new SearchResultComparator());
		this.queryToResultsMap.put(doc.getId(), queryResults);
		Utilities.printSearchResults(outputFile, Integer.toString(doc.getId()), queryResults);
	    }
	} else {
	    System.out.println("Error: no documents were read from query file");
	}
    }

    public void Start() {

	if (!initVariables()) {
	    System.out.println("Error: initiating variables from properties file failed");
	    System.exit(2);
	}

	// read documents from file and build index
	indexDocuments();

	// read queries from file and search against index
	search();

	testing();

	System.out.println("Info: Finished searching");
    }

    private void testing() {

	Map<Integer, QueryIdealResult> queryTruthResults = Utilities.parseTruthLists(this.truthFilePath);

	for (Map.Entry<Integer, List<SearchResult>> queryResult : this.queryToResultsMap.entrySet()) {

	    List<Integer> docsIds = SearchResult.extractIds(queryResult);

	    QueryIdealResult queryIdealResult = queryTruthResults.get(queryResult.getKey());
	    if (queryIdealResult == null) {
		continue;
	    }
	    double ndcgValue = queryIdealResult.calcNDCG(docsIds);
	    double precisionAt5 = Utilities.PrecisionAtN(5, docsIds, queryIdealResult);
	    double precisionAt10 = Utilities.PrecisionAtN(10, docsIds, queryIdealResult);
	    double meanAveragePrecision = Utilities.meanAveragePrecision(docsIds, queryIdealResult);
	    System.out.println("NDCG: " + ndcgValue + " presi5: " + precisionAt5 + " presi10 " + precisionAt10
		    + " meanAvgPre: " + meanAveragePrecision);
	}
    }

}
