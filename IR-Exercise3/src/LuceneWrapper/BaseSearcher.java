package LuceneWrapper;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
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

public class BaseSearcher {

    private Directory       luceneDir;
    protected IndexSearcher searcher;
    private IndexReader     reader;
    protected Analyzer      analyzer;

    public BaseSearcher(Directory luceneDir) {
	this.luceneDir = luceneDir;
	this.analyzer = new StandardAnalyzer(Version.LUCENE_47);
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
	    Query query = new QueryParser(Version.LUCENE_47, "content", this.analyzer).parse(queryStr);
	    TopDocs topDocs = this.searcher.search(query, Integer.MAX_VALUE);

	    if (topDocs.totalHits > 0) {
		docs = Arrays.asList(topDocs.scoreDocs);
	    }

	} catch (ParseException e) {
	    e.printStackTrace();
	}

	return docs;
    }

}
