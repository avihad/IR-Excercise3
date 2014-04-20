package LuceneWrapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import entities.IRDoc;
import entities.SearchResult;

public class BaseSearchEngine implements ISearchEngine {

    public static ISearchEngine createEngine(String algType) throws IOException {
	ISearchEngine searchEngine;

	if (algType != null && algType.equalsIgnoreCase("improved")) {
	    searchEngine = new ImprovedSearchEngine("improved_lucene_index");
	} else {
	    searchEngine = new BaseSearchEngine("base_lucene_index");
	}
	return searchEngine;
    }

    protected boolean      indexChanged = false;
    protected BaseIndexer  indexer;
    protected Directory    luceneDir;
    protected BaseSearcher searcher;

    protected BaseSearchEngine(String sIndexDir) throws IOException {
	this.luceneDir = FSDirectory.open(new File(sIndexDir));
    }

    protected synchronized BaseIndexer GetIndexWriter() {

	if (this.indexer == null) {
	    BaseIndexer indexer = new BaseIndexer(this.luceneDir);

	    if (indexer.OpenIndexWriter()) {
		this.indexer = indexer;
	    }
	}

	return this.indexer;
    }

    protected synchronized BaseSearcher GetSearcher() {
	if (this.searcher == null || this.indexChanged) {
	    try {
		if (this.searcher != null) {
		    this.searcher.close();
		}

		this.searcher = new BaseSearcher(this.luceneDir);
		this.searcher.Init();
	    } catch (IOException e) {
		this.searcher = null;
		// TODO Auto-generated catch block
		e.printStackTrace();
	    } finally {
		this.indexChanged = false;
	    }
	}

	return this.searcher;
    }

    /**
     * Index a list of documents into lucene engine
     */

    @Override
    public Boolean index(List<IRDoc> documents) {
	BaseIndexer indexer = GetIndexWriter();
	Integer indexedDocsCount = 0;

	if (indexer != null) {
	    Document doc;
	    for (IRDoc myDoc : documents) {
		doc = indexer.getDocument(myDoc.getId(), myDoc.getContent());

		if (doc != null) {
		    try {
			indexer.index(doc);
			indexedDocsCount++;
		    } catch (IOException e) {
			e.printStackTrace();
		    }
		}
	    }
	    this.indexChanged = true;

	    indexer.closeIndexWriter();
	}

	return documents.size() == indexedDocsCount;
    }

    @Override
    public List<SearchResult> search(String query) {
	List<SearchResult> result = new LinkedList<SearchResult>();
	List<String> docsIdsCovered = new ArrayList<String>();
	BaseSearcher searcher = GetSearcher();

	if (searcher != null) {
	    try {
		List<ScoreDoc> docs = searcher.search(query);

		String id;
		for (ScoreDoc doc : docs) {
		    Document tempDoc = searcher.getDoc(doc.doc);
		    id = tempDoc.get("id");
		    // FIXME: need to give a proper threshold
		    if (!docsIdsCovered.contains(id) && doc.score > 0.1) {
			result.add(new SearchResult(id, doc.score));
			docsIdsCovered.add(id);
		    }
		}

	    } catch (IOException e) {
		e.printStackTrace();
	    }
	}

	return result;
    }
}
