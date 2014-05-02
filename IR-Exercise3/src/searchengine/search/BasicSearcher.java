package searchengine.search;

import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.Version;

public class BasicSearcher {

	protected final List<String> stopwords = Arrays.asList("a", "an", "and", "are", "as", "at", "be", "but",
			   "by", "for", "if", "in", "into", "is", "it", "no", "not",
			   "of", "on", "or", "such", "that", "the", "their", "then",
			   "there", "these", "they", "this", "to", "was", "will",
			   "with");
	
    private Directory       luceneDir;
    protected IndexSearcher searcher;
    private IndexReader     reader;
    protected Analyzer      analyzer;

    public BasicSearcher(Directory luceneDir) {
	this.luceneDir = luceneDir;
    }

    public void init()
    {
    	initAnalyzer();
    }
    
    public void close() {
	try {
	    if (this.reader != null) {
		this.reader.close();

	    }
	} catch (IOException e) {
	    e.printStackTrace();
	} finally {
	    this.reader = null;
	    this.searcher = null;
	}
    }

    public Document getDoc(int docId) {
	Document doc = null;

	try {
	    doc = this.searcher.doc(docId);
	} catch (IOException e) {
	    System.out.println("Error: unable to fetch doc with id=" + docId);
	    e.printStackTrace();
	}

	return doc;
    }

    public void Init() throws IOException {
	this.reader = DirectoryReader.open(this.luceneDir);
	this.searcher = new IndexSearcher(this.reader);
    }

    @SuppressWarnings("unchecked")
    public List<ScoreDoc> search(String queryStr) throws IOException {
	List<ScoreDoc> docs = Collections.EMPTY_LIST;

	try {
	    QueryParser queryParser = new QueryParser(Version.LUCENE_47, "content", this.analyzer);
	    String escapeQueryStr = QueryParser.escape(queryStr);
	    Query query = queryParser.parse(escapeQueryStr);
	    TopDocs topDocs = this.searcher.search(query, 10000);

	    if (topDocs.totalHits > 0) {
		docs = Arrays.asList(topDocs.scoreDocs);
	    }

	} catch (ParseException e) {
	    e.printStackTrace();
	}

	return docs;
    }
    
    public boolean setStopWords(List<String> stopWords)
    {
    	boolean result = false;
    	
    	if(stopWords != null)
    	{
    		this.stopwords.clear();
    		result = this.stopwords.addAll(stopWords);
    	}
    	
    	return result;
    }
    

    public void TestAnalyzer() {
	try {
	    TokenStream stream = this.analyzer.tokenStream("myfield", new StringReader(
		    "hello there three word should not be present"));

	    CharTermAttribute termAtt = stream.addAttribute(CharTermAttribute.class);

	    try {
		stream.reset();

		while (stream.incrementToken()) {
		    System.out.println(termAtt.toString());
		}

		stream.end();
	    } finally {
		stream.close();
	    }
	} catch (Exception ex) {

	}
    }

    protected void initAnalyzer() {
    	CharArraySet set = new CharArraySet(Version.LUCENE_47, stopwords, true);
    	this.analyzer = new StandardAnalyzer(Version.LUCENE_47, set);

    }

}
