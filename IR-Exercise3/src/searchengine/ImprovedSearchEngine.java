package searchengine;

import java.io.IOException;

import searchengine.index.BasicIndexer;
import searchengine.index.ImprovedIndexer;
import searchengine.search.BasicSearcher;
import searchengine.search.ImprovedSearcher;
import entities.ImprovedIRDoc;

/**
 * Search engine using {@link ImprovedIndexer} and {@link ImprovedSearcher} to index and search {@link ImprovedIRDoc}
 * */
public class ImprovedSearchEngine extends BasicSearchEngine {

    public ImprovedSearchEngine(String indexDir) throws IOException {
	super(indexDir);
    }

    @Override
    protected synchronized BasicIndexer getIndexWriter() {

	if (this.indexer == null) {
	    BasicIndexer indexer = new ImprovedIndexer(this.luceneDir);
	    indexer.setStopWords(this.stopwords);

	    if (indexer.OpenIndexWriter()) {
		this.indexer = indexer;
	    }
	}

	return this.indexer;
    }

    @Override
    protected synchronized BasicSearcher getSearcher() {
	if (this.searcher == null || this.indexChanged) {
	    try {
		if (this.searcher != null) {
		    this.searcher.close();
		}
		this.searcher = new ImprovedSearcher(this.luceneDir);
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
}
