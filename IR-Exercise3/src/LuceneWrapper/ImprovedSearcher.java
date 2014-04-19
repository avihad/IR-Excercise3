package LuceneWrapper;

import java.io.IOException;
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
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.Version;

public class ImprovedSearcher extends BaseSearcher{

	protected final List<String> stopwords = new LinkedList<String>(Arrays.asList("a","an","and","are","as","at","be","but","by","for","if","in","into","is","it","no","not","of","on","or","such","that","the","their","then","there","these","they","this","to","was","will","with"));
	
	public ImprovedSearcher(Directory luceneDir)
	{
		super(luceneDir);
		initAnalyzer();
		TestAnalyzer();
	}
	
	@Override
	public List<ScoreDoc> search(String queryStr) throws IOException
	{
		//FIXME: will need to manipulate query string 
		return super.search(queryStr);
	}
	
	protected void initAnalyzer()
    {
			Analyzer analyzer = new Analyzer() {
				@Override
				protected TokenStreamComponents createComponents(String fieldName, Reader reader) {
					Tokenizer source = new WhitespaceTokenizer(Version.LUCENE_47, reader);
					TokenStream tok = new StandardFilter(Version.LUCENE_47, source);
					tok = new LowerCaseFilter(Version.LUCENE_47, tok);
					// tok = new ASCIIFoldingFilter(tok);
					// tok = new NGramTokenFilter(Version.LUCENE_47, tok, 2, 20);
					tok = new StopFilter(Version.LUCENE_47, tok, new CharArraySet(Version.LUCENE_47, stopwords, true));
					return new TokenStreamComponents(source, tok);
				}
			};
			// FIXME: will need to create our own analyzer
			this.analyzer = analyzer;
    }
}
