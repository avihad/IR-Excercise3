package LuceneWrapper;

import java.io.Reader;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.core.WhitespaceTokenizer;
import org.apache.lucene.analysis.ngram.NGramTokenFilter;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.document.Document;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.Version;

public class ImprovedIndexer extends BaseIndexer{
	
	protected final List<String> stopwords = new LinkedList<String>(Arrays.asList("a","an","and","are","as","at","be","but","by","for","if","in","into","is","it","no","not","of","on","or","such","that","the","their","then","there","these","they","this","to","was","will","with"));
	
	public ImprovedIndexer(Directory luceneDir)
	{
		super(luceneDir);
		
	}
	
	@Override
	public Document getDocument(int docId, String content) {
		//FIXME: may need to create a document that has more than just 2 fields (i.e. id, content)
		return super.getDocument(docId, content);
	    }
	
	@Override
	protected Analyzer GetAnalzyer() {
		Analyzer analyzer = new Analyzer() {
			@Override
			protected TokenStreamComponents createComponents(String fieldName, Reader reader) {
				Tokenizer source = new WhitespaceTokenizer(Version.LUCENE_47, reader);
				TokenStream tok = new StandardFilter(Version.LUCENE_47, source);
				tok = new LowerCaseFilter(Version.LUCENE_47, tok);
				// tok = new ASCIIFoldingFilter(tok);
				tok = new StopFilter(Version.LUCENE_47, tok, new CharArraySet(Version.LUCENE_47, stopwords, true));
				tok = new NGramTokenFilter(Version.LUCENE_47, tok, 2, 20);
				return new TokenStreamComponents(source, tok);
			}
		};

		return analyzer;
	}
}
