package searchengine;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.lucene.document.Document;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import searchengine.index.BasicIndexer;
import searchengine.search.BasicSearcher;
import entities.EngineStrategy;
import entities.IRDoc;
import entities.SearchResult;

public class BasicSearchEngine implements ISearchEngine {

    public static ISearchEngine createEngine(EngineStrategy strategy) throws IOException {
	ISearchEngine searchEngine;

	switch (strategy) {
	case Improved:
	    searchEngine = new ImprovedSearchEngine("improved_lucene_index");
	    break;
	case Basic:
	default:
	    searchEngine = new BasicSearchEngine("base_lucene_index");
	    break;

	}

	return searchEngine;
    }

    protected boolean indexChanged;
    protected BasicIndexer indexer;
    protected Directory luceneDir;
    protected BasicSearcher searcher;
    protected List<IRDoc> idexedDocs;
    protected List<String> stopwords;
    protected Map<Integer, Integer> referencesCountMap;

    protected BasicSearchEngine(String sIndexDir) throws IOException {
	this.luceneDir = FSDirectory.open(new File(sIndexDir));
	this.indexChanged = false;
	this.stopwords = null;
	this.referencesCountMap = new HashMap<Integer, Integer>();
    }

    protected void createDocReferencesMap(List<IRDoc> documents) {
	Integer temp;
	for (IRDoc doc : documents) {
	    if (doc.getReferences() != null) {
		for (Integer id : doc.getReferences()) {
		    temp = this.referencesCountMap.get(id);

		    if (temp == null || temp == 0) {
			this.referencesCountMap.put(id, 1);
		    } else {
			temp++;
			this.referencesCountMap.put(id, temp);
		    }
		}
	    }
	}
    }

    protected synchronized BasicIndexer getIndexWriter() {

	if (this.indexer == null) {
	    BasicIndexer indexer = new BasicIndexer(this.luceneDir);
	    indexer.setStopWords(this.stopwords);

	    if (indexer.OpenIndexWriter()) {
		this.indexer = indexer;
	    }
	}

	return this.indexer;
    }

    protected synchronized BasicSearcher getSearcher() {
	if (this.searcher == null || this.indexChanged) {
	    try {
		if (this.searcher != null) {
		    this.searcher.close();
		}

		this.searcher = new BasicSearcher(this.luceneDir);
		this.searcher.setStopWords(this.stopwords);
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
	BasicIndexer indexer = getIndexWriter();
	Integer indexedDocsCount = 0;

	if (indexer != null) {
	    Document doc;
	    for (IRDoc myDoc : documents) {
		doc = myDoc.createDocument();

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
    public List<SearchResult> search(IRDoc irDoc) {
	List<SearchResult> result = new LinkedList<SearchResult>();
	List<String> docsIdsCovered = new ArrayList<String>();
	BasicSearcher searcher = getSearcher();

	if (searcher != null) {
	    try {
		List<ScoreDoc> docs = searcher.search(irDoc);
		String id;
		for (ScoreDoc doc : docs) {
		    Document tempDoc = searcher.getDoc(doc.doc);
		    id = tempDoc.get("id");
		    // FIXME: need to give a proper threshold
		    if (!docsIdsCovered.contains(id) && doc.score > 0.05) {
			result.add(new SearchResult(Integer.valueOf(id), doc.score));
			docsIdsCovered.add(id);
		    }
		}

	    } catch (IOException e) {
		e.printStackTrace();
	    }
	}

	return result;
    }

    private IRDoc setDocReferences(IRDoc myDoc) {

	IRDoc doc = myDoc.Clone();
	Integer refCount = this.referencesCountMap.get(myDoc.getId());

	if (refCount != null && refCount > 0) {
	    doc.setDocBoost((refCount / 10.0f) + 1);
	}

	return doc;
    }

    @Override
    public void setStopwords(List<String> stopwords) {
	this.stopwords = stopwords;
    }

}
