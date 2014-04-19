package LuceneWrapper;

import java.io.IOException;

public class ImprovedLuceneWrapper extends BaseLuceneWrapper {

	public ImprovedLuceneWrapper(String indexDir) throws IOException {
		super(indexDir);
	}

	protected synchronized BaseIndexer GetIndexWriter() {

		if (this._Indexer == null) {
			BaseIndexer indexer = new ImprovedIndexer(this._LuceneDir);

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
				this._Searcher = new ImprovedSearcher(this._LuceneDir);
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
}
