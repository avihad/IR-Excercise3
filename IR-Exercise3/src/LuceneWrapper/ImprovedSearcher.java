package LuceneWrapper;

import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;
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

public class ImprovedSearcher extends BaseSearcher {

    protected final List<String> stopwords = Arrays.asList("a", "an", "and", "are", "as", "at", "be", "but",
						   "by", "for", "if", "in", "into", "is", "it", "no", "not",
						   "of", "on", "or", "such", "that", "the", "their", "then",
						   "there", "these", "they", "this", "to", "was", "will",
						   "with");

    public ImprovedSearcher(Directory luceneDir) {
	super(luceneDir);
	initAnalyzer();
	TestAnalyzer();
    }

    protected void initAnalyzer() {
	Analyzer analyzer = new Analyzer() {
	    @Override
	    protected TokenStreamComponents createComponents(String fieldName, Reader reader) {
		Tokenizer source = new WhitespaceTokenizer(Version.LUCENE_47, reader);
		TokenStream token = new StandardFilter(Version.LUCENE_47, source);
		token = new LowerCaseFilter(Version.LUCENE_47, token);
		// token = new ASCIIFoldingFilter(token);
		// token = new NGramTokenFilter(Version.LUCENE_47, token, 2, 20);
		token = new StopFilter(Version.LUCENE_47, token, new CharArraySet(Version.LUCENE_47,
			ImprovedSearcher.this.stopwords, true));
		return new TokenStreamComponents(source, token);
	    }
	};
	// FIXME: will need to create our own analyzer
	this.analyzer = analyzer;
    }

    @Override
    public List<ScoreDoc> search(String queryStr) throws IOException {
	// FIXME: will need to manipulate query string
	return super.search(queryStr);
    }
}
