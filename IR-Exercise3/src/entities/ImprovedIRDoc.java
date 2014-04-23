package entities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.TextField;
import org.apache.lucene.document.Field;

import utils.Pair;
import utils.Utilities;

public class ImprovedIRDoc extends BasicIRDoc {

    public static IRDoc create(int id, String content) {
	Pair<String, List<Integer>> contentReferencesPair = sparateContentAndReferences(content);
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

    private static List<Integer> parseReferences(String referencesString) {
	List<Integer> references = new ArrayList<Integer>();
	String[] refStrings = referencesString.split(",");

		for (String reference : refStrings) {
			try {
				int ref = Integer.parseInt(reference.trim());
				references.add(ref);
			} catch (Exception ex) {
			}
		}

	return references;
    }

    @SuppressWarnings("unchecked")
    private static Pair<String, List<Integer>> sparateContentAndReferences(String content) {

	String fixedContent = content;
	List<Integer> references = Collections.EMPTY_LIST;
	String referenceIndication = "References:";
	int referencesIndex = content.lastIndexOf(referenceIndication);
	if (referencesIndex > 0) {
	    fixedContent = content.substring(0, referencesIndex);
	    references = parseReferences(content.substring(referencesIndex + referenceIndication.length()));
	}
	return Pair.of(fixedContent, references);
    }

    private final List<Integer> references;
    private final List<String>  dates;
    private final List<String>  keywords;
    private final String	title;

    private ImprovedIRDoc(int docId, String content, String title, List<Integer> references,
	    List<String> dates, List<String> keywords) {
	super(docId, content);
	this.references = Collections.unmodifiableList(references);
	this.dates = Collections.unmodifiableList(dates);
	this.keywords = Collections.unmodifiableList(keywords);
	this.title = title;

    }

    @Override
    public Document createDocument() {
    	Document newDoc = super.createDocument();
    	newDoc.add(new TextField("references", Utilities.GenericJoinToStr(this.references, " "), Field.Store.YES));
    	newDoc.add(new TextField("keywords", Utilities.GenericJoinToStr(this.keywords, " "), Field.Store.YES));
    	newDoc.add(new TextField("dates", Utilities.GenericJoinToStr(this.dates, " "), Field.Store.YES));

    	return newDoc;
    }

    public List<String> getDates() {
	return this.dates;
    }

    public List<String> getKeywords() {
	return this.keywords;
    }

    public List<Integer> getReferences() {
	return this.references;
    }

    public String getTitle() {
	return this.title;
    }

}
