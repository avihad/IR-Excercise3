package entities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import utils.Pair;

public class ImprovedIRDoc extends BasicIRDoc {

    public static IRDoc create(int id, String content) {
	Pair<String, List<Integer>> contentReferencesPair = sparateContentAndReferences(content);
	List<Integer> docReferences = contentReferencesPair.second;
	String docContent = contentReferencesPair.first;
	String title = extractTitle(content);
	List<String> dates = extractDates(content);
	List<String> keywords = extractDates(content);

	return new ImprovedIRDoc(id, docContent, title, docReferences, dates, keywords);
    }

    /**
     * extract all the dates from the content in the format "JB <DATE> AM\PM"
     * */
    private static List<String> extractDates(String content) {

	List<String> dates = new ArrayList<String>();
	// FIXME: change to the correct length
	final int dateMaxLength = 30;
	int startIndex = 0;
	int endIndex = 0;

	while (startIndex > -1 && endIndex > -1) {
	    startIndex = content.indexOf("JB", startIndex);
	    endIndex = content.indexOf("PM");
	    if (endIndex > -1 && (endIndex - startIndex) <= dateMaxLength) {
		// TODO: change indexes
		dates.add(content.substring(startIndex, endIndex));
	    } else {
		endIndex = content.indexOf("AM");
		if (endIndex > -1 && (endIndex - startIndex) <= dateMaxLength) {
		    // TODO: change indexes
		    dates.add(content.substring(startIndex, endIndex));
		}

	    }
	    startIndex = endIndex;
	}
	return dates;
    }

    private static String extractTitle(String content) {
	// TODO Auto-generated method stub
	return null;
    }

    private static List<Integer> parseReferences(String referencesString) {
	List<Integer> references = new ArrayList<Integer>();
	String[] refStrings = referencesString.split(",");

	for (String reference : refStrings) {
	    int ref = Integer.parseInt(reference);
	    references.add(ref);
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

    public ImprovedIRDoc(int docId, String content, String title, List<Integer> references,
	    List<String> dates, List<String> keywords) {
	super(docId, content);
	this.references = Collections.unmodifiableList(references);
	this.dates = Collections.unmodifiableList(dates);
	this.keywords = Collections.unmodifiableList(keywords);
	this.title = title;

    }

    public List<Integer> getReferences() {
	return this.references;
    }

}
