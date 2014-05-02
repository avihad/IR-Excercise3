package searchengine.index;

import java.io.Reader;

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

public class ImprovedIndexer extends BasicIndexer {

    public ImprovedIndexer(Directory luceneDir) {
	super(luceneDir);

    }

    @Override
    protected Analyzer createAnalzyer() {
	Analyzer analyzer = new Analyzer() {
	    @Override
	    protected TokenStreamComponents createComponents(String fieldName, Reader reader) {
		Tokenizer source = new WhitespaceTokenizer(Version.LUCENE_47, reader);
		TokenStream token = new StandardFilter(Version.LUCENE_47, source);
		token = new LowerCaseFilter(Version.LUCENE_47, token);
		// token = new ASCIIFoldingFilter(tok);
		token = new StopFilter(Version.LUCENE_47, token, new CharArraySet(Version.LUCENE_47,
			ImprovedIndexer.this.stopwords, true));
		token = new NGramTokenFilter(Version.LUCENE_47, token, 2, 20);
		return new TokenStreamComponents(source, token);
	    }
	};

	return analyzer;
    }

    @Override
    public Document createDocument(int docId, String content) {
	// FIXME: may need to create a document that has more than just 2 fields (i.e. id, content)
	return super.createDocument(docId, content);
    }
}
