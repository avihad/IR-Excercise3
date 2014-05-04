package searchengine.search;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.Version;

import utils.Utilities;
import entities.IRDoc;
import entities.ImprovedIRDoc;

/**
 * Search for {@link ImprovedIRDoc} and setting boosts for special fields in the IRDoc
 * */
public class ImprovedSearcher extends BasicSearcher {

    private static final float DATES_BOOST = 3.0f;
    private static final float REF_BOOST = 2.0f;
    private static final float KEYWORD_BOOST = 2.0f;
    private static final float TITLE_BOOST = 2.0f;

    public ImprovedSearcher(Directory luceneDir) {
	super(luceneDir);
    }

    /**
     * Helper to append special field to a query string with a boost for similarity
     * */
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

    /**
     * Generate improved query from the special fields extracted in {@link ImprovedIRDoc} and give them boost for
     * similarity
     * */
    private String improveQuery(ImprovedIRDoc doc) {
	StringBuilder queryBuilder = new StringBuilder(QueryParser.escape(doc.getContent()));

	List<String> dates = doc.getDates();
	if (dates != null && dates.size() > 0) {
	    appendFieldToQuery(queryBuilder, "dates", Utilities.GenericJoinToStr(dates, " "), DATES_BOOST);
	}

	List<String> references = new ArrayList<String>();

	List<Integer> referencesInt = doc.getReferences();
	for (Integer ref : referencesInt) {
	    references.add("" + ref);
	}

	if (references != null && references.size() > 0) {
	    appendFieldToQuery(queryBuilder, "references",
		    QueryParser.escape(Utilities.GenericJoinToStr(references, " ")), REF_BOOST);
	}

	List<String> keywords = doc.getKeywords();
	if (keywords != null && keywords.size() > 0) {
	    appendFieldToQuery(queryBuilder, "keywords", QueryParser.escape(Utilities.GenericJoinToStr(keywords, " ")),
		    KEYWORD_BOOST);
	}

	String title = doc.getTitle();
	if (title != null && !title.isEmpty()) {
	    appendFieldToQuery(queryBuilder, "title", QueryParser.escape(title), TITLE_BOOST);
	}

	String query = queryBuilder.toString();

	return query;
    }

    @Override
    protected void initAnalyzer() {
	/*
	 * CharArraySet set = new CharArraySet(Version.LUCENE_47, stopwords, true); this.analyzer = new
	 * StandardAnalyzer(Version.LUCENE_47, set);
	 */
	Analyzer analyzer = new Analyzer() {
	    @Override
	    protected TokenStreamComponents createComponents(String fieldName, Reader reader) {
		Tokenizer source = new StandardTokenizer(Version.LUCENE_47, reader);
		TokenStream token = new LowerCaseFilter(Version.LUCENE_47, source);
		token = new StopFilter(Version.LUCENE_47, token, new CharArraySet(Version.LUCENE_47,
			ImprovedSearcher.this.stopwords, true));
		return new TokenStreamComponents(source, new PorterStemFilter(token));
	    }
	};

	this.analyzer = analyzer;
    }

    /**
     * Search for an IRDoc
     * */
    @Override
    @SuppressWarnings("unchecked")
    public List<ScoreDoc> search(IRDoc irDoc) throws IOException {
	if (!(irDoc instanceof ImprovedIRDoc)) {
	    return super.search(irDoc);
	}
	ImprovedIRDoc impIrDoc = (ImprovedIRDoc) irDoc;
	List<ScoreDoc> docs = Collections.EMPTY_LIST;

	try {
	    QueryParser queryParser = new QueryParser(Version.LUCENE_47, "content", this.analyzer);
	    // improves query by trying to parse known fields and boost their match scoring.
	    String improvedQuery = improveQuery(impIrDoc);
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
