package searchengine.index;

import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.Version;

public class BasicIndexer {
	protected final List<String> stopwords = Arrays.asList("a", "an", "and", "are", "as", "at", "be", "but",
			   "by", "for", "if", "in", "into", "is", "it", "no", "not",
			   "of", "on", "or", "such", "that", "the", "their", "then",
			   "there", "these", "they", "this", "to", "was", "will",
			   "with");
	
    protected IndexWriter writer;
    private Directory     luceneDir;

    public BasicIndexer(Directory luceneDir) {
	this.luceneDir = luceneDir;
    }

    public void closeIndexWriter() {
	if (this.writer != null) {
	    try {
		this.writer.close();
		this.writer = null;
	    } catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }
	}
    }

    public Document createDocument(int docId, String content) {
	Document newDoc = new Document();
	newDoc.add(new TextField("content", content, Field.Store.YES));
	newDoc.add(new LongField("id", docId, Field.Store.YES));

	return newDoc;
    }

    public void index(Document doc) throws IOException {
	if (this.writer != null) {
	    this.writer.addDocument(doc);
	}
    }

    public boolean OpenIndexWriter() {
	boolean initResult = false;
	try {

	    IndexWriterConfig luceneConfig = new IndexWriterConfig(Version.LUCENE_47, createAnalzyer());
	    luceneConfig.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);

	    this.writer = new IndexWriter(this.luceneDir, luceneConfig);

	    initResult = true;
	} catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}

	return initResult;
    }
    
    protected Analyzer createAnalzyer()
    {
    	CharArraySet set = new CharArraySet(Version.LUCENE_47, stopwords, true);
    	Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_47, set);
    	
    	return analyzer;
    }
    
    public boolean setStopWords(List<String> stopWords)
    {
    	boolean result = false;
    	
    	if(stopWords != null)
    	{
    		this.stopwords.clear();
    		result = this.stopwords.addAll(stopWords);
    	}
    	
    	return result;
    }
    
    public void TestAnalyzer()
	{
		try
		{
	    Analyzer analyzer = createAnalzyer();
	    TokenStream stream = analyzer.tokenStream("myfield", new StringReader("hello there three word should not be present"));

	    CharTermAttribute termAtt = stream.addAttribute(CharTermAttribute.class);
	    
	    try {
	        stream.reset();
	      
	        while (stream.incrementToken()) {
	          System.out.println(termAtt.toString());
	        }
	      
	        stream.end();
	      } finally {
	        stream.close();
	      }
		}catch(Exception ex)
		{
			
		}
	}
}
