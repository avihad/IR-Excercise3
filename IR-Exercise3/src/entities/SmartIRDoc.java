package entities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import utils.Pair;

public class SmartIRDoc extends SimpleIRDoc {

    public static IRDoc create(int id, String content) {
	Pair<String, List<Integer>> contentReferencesPair = sparateContentAndReferences(content);

	return new SmartIRDoc(id, contentReferencesPair.first, contentReferencesPair.second);
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

    public SmartIRDoc(int docId, String content, List<Integer> references) {
	super(docId, content);
	this.references = Collections.unmodifiableList(references);

    }

    public List<Integer> getReferences() {
	return this.references;
    }

}
