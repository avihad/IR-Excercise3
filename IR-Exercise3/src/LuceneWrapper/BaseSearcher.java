package LuceneWrapper;

import java.io.IOException;

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

    private Directory       _luceneDir;
    protected IndexSearcher _indexSearcher;
    private IndexReader     _reader;
    protected Analyzer      _analyzer;

    public BaseSearcher(Directory luceneDir) {
	this._luceneDir = luceneDir;
	this._analyzer = new StandardAnalyzer(Version.LUCENE_47);
    }

    public void Close() {
	try {
	    if (this._reader != null) {
		this._reader.close();

	    }
	} catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} finally {
	    this._reader = null;
	    this._indexSearcher = null;
	}
    }

    public Document GetDoc(int docId) {
	Document doc = null;

	try {
	    doc = this._indexSearcher.doc(docId);
	} catch (IOException e) {
	    System.out.println("Error: unable to fetch doc with id=" + docId);
	    e.printStackTrace();
	}

	return doc;
    }

    public void Init() throws IOException {
	this._reader = DirectoryReader.open(this._luceneDir);
	this._indexSearcher = new IndexSearcher(this._reader);
    }

    public ScoreDoc[] Search(String queryStr) throws IOException {
	ScoreDoc docs[] = null;

	try {
	    Query query = new QueryParser(Version.LUCENE_47, "content", this._analyzer).parse(queryStr);
	    TopDocs topDocs = this._indexSearcher.search(query, Integer.MAX_VALUE);

	    if (topDocs.totalHits > 0) {
		docs = topDocs.scoreDocs;
	    }

	} catch (ParseException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}

	return docs;
    }

}
