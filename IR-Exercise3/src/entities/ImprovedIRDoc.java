package entities;

import java.util.Collections;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;

import utils.Pair;
import utils.Utilities;

/**
 * Improved IRDoc is an {@link IRDoc} extends the BasocIRDoc that separate the document into id , dates, keyword and
 * title
 * */
public class ImprovedIRDoc extends BasicIRDoc {

    /**
     * Factory method - convenience method for creation of ImprovedIRDoc
     * */
    public static IRDoc create(int id, String content) {
	Pair<String, List<Integer>> contentReferencesPair = sperateContentAndReferences(content);
	List<Integer> docReferences = contentReferencesPair.second;
	String docContent = contentReferencesPair.first;
	String title = extractTitle(content);
	List<String> dates = Utilities.extractDates(content);
	List<String> keywords = Utilities.extractKeywords(content);

	return new ImprovedIRDoc(id, docContent, title, docReferences, dates, keywords);
    }

    /**
     * return a substring of content from the beginning until the first comma
     * */
    private static String extractTitle(String content) {

	int firstComma = content.indexOf(",");
	if (firstComma > 0) {
	    return content.substring(0, firstComma);
	} else {
	    return "";
	}
    }

    private final List<String> dates;
    private final List<String> keywords;
    private final String title;

    private ImprovedIRDoc(int docId, String content, String title, List<Integer> references, List<String> dates,
	    List<String> keywords) {
	super(docId, content, references);
	this.dates = Collections.unmodifiableList(dates);
	this.keywords = Collections.unmodifiableList(keywords);
	this.title = title;

    }

    @Override
    public ImprovedIRDoc Clone() {
	ImprovedIRDoc newDoc = new ImprovedIRDoc(this.getId(), this.getContent(), this.getTitle(),
		this.getReferences(), this.getDates(), this.getKeywords());
	return newDoc;
    }

    @Override
    public Document createDocument() {
	Document newDoc = super.createDocument();

	Field f;

	f = new TextField("references", Utilities.GenericJoinToStr(this.references, " "), Field.Store.YES);
	f.setBoost(this.boost);
	newDoc.add(f);

	f = new TextField("keywords", Utilities.GenericJoinToStr(this.keywords, " "), Field.Store.YES);
	f.setBoost(this.boost);
	newDoc.add(f);

	f = new TextField("title", this.title, Field.Store.YES);
	f.setBoost(this.boost);
	newDoc.add(f);

	f = new TextField("dates", Utilities.GenericJoinToStr(this.dates, " "), Field.Store.YES);
	f.setBoost(this.boost);
	newDoc.add(f);

	return newDoc;
    }

    public List<String> getDates() {
	return this.dates;
    }

    public List<String> getKeywords() {
	return this.keywords;
    }

    public String getTitle() {
	return this.title;
    }

}
