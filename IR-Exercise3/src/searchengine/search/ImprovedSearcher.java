package searchengine.search;

import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.core.WhitespaceTokenizer;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.Version;

import utils.Utilities;

public class ImprovedSearcher extends BasicSearcher {

    private static final float DATES_BOOST = 3.0f;
    private static final float REF_BOOST = 2.0f;
    private static final float KEYWORD_BOOST = 2.0f;


    public ImprovedSearcher(Directory luceneDir) {
	super(luceneDir);
    }

    private void appendFieldToQuery(StringBuilder str, String field, String value, float boost) {
	str.append(" OR ");
	str.append(field);
	str.append(":(");
	str.append(value);
	str.append(")");

	if (boost != 1.0) {
	    str.append("^");
	    str.append(boost);
	}
    }

    private String improveQuery(String queryStr) {
	StringBuilder queryBuilder = new StringBuilder(QueryParser.escape(queryStr));

	List<String> dates = Utilities.extractDates(queryStr);
	if (dates != null && dates.size() > 0) {
	    appendFieldToQuery(queryBuilder, "dates", Utilities.GenericJoinToStr(dates, " "), DATES_BOOST);
	}

	List<String> references = Utilities.extractReferences(queryStr);
	if (references != null && references.size() > 0) {
	    appendFieldToQuery(queryBuilder, "references", Utilities.GenericJoinToStr(references, " "), REF_BOOST);
	}

	List<String> keywords = Utilities.extractKeywords(queryStr);
	if (keywords != null && keywords.size() > 0) {
	    appendFieldToQuery(queryBuilder, "keywords", Utilities.GenericJoinToStr(keywords, " "), KEYWORD_BOOST);
	}
	
	String query = queryBuilder.toString().replace(" or ", " OR ").replace(" and ", " AND ");

	return query;
    }

    @Override
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
	
	this.analyzer = analyzer;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<ScoreDoc> search(String queryStr) throws IOException {
	List<ScoreDoc> docs = Collections.EMPTY_LIST;

	try {
	    QueryParser queryParser = new QueryParser(Version.LUCENE_47, "content", this.analyzer);
	    // improves query by trying to parse known fields and boost their match scoring.
	    String improvedQuery = improveQuery(queryStr);
	    Query query = queryParser.parse(improvedQuery);
	    TopDocs topDocs = this.searcher.search(query, 10000);

	    if (topDocs.totalHits > 0) {
		docs = Arrays.asList(topDocs.scoreDocs);
	    }

	} catch (ParseException e) {
	    e.printStackTrace();
	}

	return docs;
    }
}
