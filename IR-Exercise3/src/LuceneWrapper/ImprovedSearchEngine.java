package LuceneWrapper;

import java.io.IOException;

public class ImprovedSearchEngine extends BaseSearchEngine {

	public ImprovedSearchEngine(String indexDir) throws IOException {
		super(indexDir);
	}

	protected synchronized BaseIndexer GetIndexWriter() {

		if (this.indexer == null) {
			BaseIndexer indexer = new ImprovedIndexer(this.luceneDir);

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
				this.searcher = new ImprovedSearcher(this.luceneDir);
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
