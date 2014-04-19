package LuceneWrapper;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class BaseLuceneWrapper implements ILuceneWrapper {

    public static ILuceneWrapper GetInstance(String algType) throws IOException {
	ILuceneWrapper wrapper;

	if (algType != null && algType.equalsIgnoreCase("improved")) {
	    wrapper = null;
	} else {
	    wrapper = new BaseLuceneWrapper("base_lucene_index");
	}
	return wrapper;
    }

    private boolean	_indexChanged = false;
    protected BaseIndexer  _Indexer;
    protected Directory    _LuceneDir;
    protected BaseSearcher _Searcher;

    private BaseLuceneWrapper(String sIndexDir) throws IOException {
	this._LuceneDir = FSDirectory.open(new File(sIndexDir));
    }

    protected synchronized BaseIndexer GetIndexWriter() {

	if (this._Indexer == null) {
	    BaseIndexer indexer = new BaseIndexer(this._LuceneDir);

	    if (indexer.OpenIndexWriter()) {
		this._Indexer = indexer;
	    }
	}

	return this._Indexer;
    }

    protected synchronized BaseSearcher GetSearcher() {
	if (this._Searcher == null || this._indexChanged) {
	    try {
		if (this._Searcher != null) {
		    this._Searcher.close();
		}

		this._Searcher = new BaseSearcher(this._LuceneDir);
		this._Searcher.Init();
	    } catch (IOException e) {
		this._Searcher = null;
		// TODO Auto-generated catch block
		e.printStackTrace();
	    } finally {
		this._indexChanged = false;
	    }
	}

	return this._Searcher;
    }

    /**
     * Index a list of documents into lucene engine
     */

    @Override
    public Boolean index(List<MyDoc> documents) {
	BaseIndexer indexer = GetIndexWriter();
	Integer indexedDocsCount = 0;

	if (indexer != null) {
	    Document doc;
	    for (MyDoc myDoc : documents) {
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
	    this._indexChanged = true;

	    indexer.closeIndexWriter();
	}

	return documents.size() == indexedDocsCount;
    }

    @Override
    public List<SearchResult> search(String query) {
	List<SearchResult> result = new LinkedList<SearchResult>();
	BaseSearcher searcher = GetSearcher();

	if (searcher != null) {
	    try {
		List<ScoreDoc> docs = searcher.Search(query);

		String id;
		for (ScoreDoc doc : docs) {
		    Document tempDoc = searcher.getDoc(doc.doc);
		    id = tempDoc.get("id");
		    result.add(new SearchResult(id, doc.score));
		}

	    } catch (IOException e) {
		e.printStackTrace();
	    }
	}

	return result;
    }
}
