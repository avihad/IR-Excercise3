package LuceneWrapper;


import java.io.IOException;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.Version;

public class BaseIndexer {
	protected IndexWriter _Writer;
	private Directory _luceneDir;
	
	public BaseIndexer(Directory luceneDir)
	{
		_luceneDir = luceneDir;
	}
	
	public boolean OpenIndexWriter()
	{
		boolean initResult = false;
		try {
			
			 IndexWriterConfig luceneConfig = new IndexWriterConfig(
					 Version.LUCENE_47, new StandardAnalyzer(Version.LUCENE_47));
			 luceneConfig.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
			 
			_Writer = new IndexWriter(_luceneDir, luceneConfig);
			
			initResult = true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return initResult;
	}
	
	public void CloseIndexWriter()
	{
		if(_Writer != null)
		{
			try {
				_Writer.close();
				_Writer = null;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void Index(Document doc) throws IOException
	{
		if(_Writer != null)
			_Writer.addDocument(doc);
	}
	
	public Document GetDocument(int docId, String content)
	{
		Document newDoc = new Document();
		newDoc.add(new TextField("content", content, Field.Store.YES));
		newDoc.add(new LongField("id", docId, Field.Store.YES));
		
		return newDoc;
	}
}
