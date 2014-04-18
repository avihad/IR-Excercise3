package LuceneWrapper;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

import org.apache.lucene.document.Document;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class BaseLuceneWrapper implements ILuceneWrapper {

	public static ILuceneWrapper GetInstance(String algType) throws IOException
	{
		ILuceneWrapper wrapper;
		
		if(algType != null && algType.equalsIgnoreCase("improved"))
		{
			wrapper = null;
		}
		else
		{
			wrapper = new BaseLuceneWrapper("base_lucene_index");
		}
		return wrapper;
	}

	private boolean _indexChanged = false;
	protected BaseIndexer _Indexer;
	protected Directory _LuceneDir;
	protected BaseSearcher _Searcher;
	
	private BaseLuceneWrapper(String sIndexDir) throws IOException
	{
		_LuceneDir = FSDirectory.open(new File(sIndexDir));
	}
	
	public Integer[] Index(MyDoc[] documents)
	{
		BaseIndexer indexer = GetIndexWriter();
		LinkedList<Integer> successfulDocs = new LinkedList<Integer>();

		if (indexer != null) {
			Document doc;
			for (MyDoc myDoc : documents) {
				doc = indexer.GetDocument(myDoc._docId, myDoc._content);

				if (doc != null)
					try {
						indexer.Index(doc);
						successfulDocs.add(myDoc._docId);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			}
			_indexChanged = true;
		}
		
		if(indexer != null)
			indexer.CloseIndexWriter();
		
		return successfulDocs.toArray(new Integer[successfulDocs.size()]);
	}
	
	protected synchronized BaseIndexer GetIndexWriter()
	{
		
		if(_Indexer == null)
		{
			BaseIndexer indexer = new BaseIndexer(_LuceneDir);
			
			if(indexer.OpenIndexWriter())
			{
				this._Indexer = indexer;
			}
		}
		
		return _Indexer;
	}
	
	public SearchResult[] Search(String query)
	{
		SearchResult[] result = null;
		BaseSearcher searcher = GetSearcher();
		
		if(searcher != null)
		{
			try {
				ScoreDoc[] docs = searcher.Search(query);
				
				if(docs != null)
				{
					result = new SearchResult[docs.length];
					String id;
					for(int i = 0; i < result.length; i++)
					{
						Document tempDoc = searcher.GetDoc(docs[i].doc);
						id = tempDoc.get("id");

						result[i] = new SearchResult(id, docs[i].score);
					}
					
				}
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return result;
		
	}
	
	protected synchronized BaseSearcher GetSearcher()
	{
		if(this._Searcher == null || this._indexChanged)
		{
			try {
				if(this._Searcher != null)
					this._Searcher.Close();
				
				this._Searcher = new BaseSearcher(this._LuceneDir);
				this._Searcher.Init();
			} catch (IOException e) {
				this._Searcher = null;
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			finally
			{
				this._indexChanged = false;
			}
		}
		
		return this._Searcher;
	}
/*	private static class TextFilesFilter implements FileFilter {
		public boolean accept(File path) {
		return path.getName().toLowerCase()
		.endsWith(".txt");
		}
}*/
}


