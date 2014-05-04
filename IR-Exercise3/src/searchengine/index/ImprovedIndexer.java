package searchengine.index;

import java.io.Reader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.Version;

/**
 * Indexer that index {@link ImprovedIRDoco}
 * */
public class ImprovedIndexer extends BasicIndexer {

    public ImprovedIndexer(Directory luceneDir) {
	super(luceneDir);

    }

    /**
     * Return a custom made analyzer using lowercase stem and stop words filters
     * */
    @Override
    protected Analyzer createAnalzyer() {
	/*
	 * CharArraySet set = new CharArraySet(Version.LUCENE_47, this.stopwords, true); return new
	 * StandardAnalyzer(Version.LUCENE_47, set);
	 */
	Analyzer analyzer = new Analyzer() {
	    @Override
	    protected TokenStreamComponents createComponents(String fieldName, Reader reader) {
		Tokenizer source = new StandardTokenizer(Version.LUCENE_47, reader);
		TokenStream token = new LowerCaseFilter(Version.LUCENE_47, source);
		token = new StopFilter(Version.LUCENE_47, token, new CharArraySet(Version.LUCENE_47,
			ImprovedIndexer.this.stopwords, true));
		return new TokenStreamComponents(source, new PorterStemFilter(token));
	    }
	};

	return analyzer;
    }
}
