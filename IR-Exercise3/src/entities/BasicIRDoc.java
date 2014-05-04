package entities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.TextField;

import utils.Pair;

public class BasicIRDoc implements IRDoc {
    public static IRDoc create(int docId, String content) {
	Pair<String, List<Integer>> contentReferencesPair = sparateContentAndReferences(content);
	List<Integer> docReferences = contentReferencesPair.second;

	return new BasicIRDoc(docId, content, docReferences);
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

    protected static Pair<String, List<Integer>> sparateContentAndReferences(String content) {

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

    private final int id;

    private final String content;

    protected float boost;

    protected final List<Integer> references;

    protected BasicIRDoc(int docId, String content, List<Integer> references) {
	this.id = docId;
	this.content = content;
	this.references = Collections.unmodifiableList(references);
	this.boost = 1.0f;
    }

    @Override
    public BasicIRDoc Clone() {
	BasicIRDoc newDoc = new BasicIRDoc(this.getId(), this.getContent(), this.getReferences());
	return newDoc;
    }

    @Override
    public Document createDocument() {
	Document newDoc = new Document();

	Field f;

	f = new TextField("content", this.content, Field.Store.YES);
	f.setBoost(this.boost);
	newDoc.add(f);

	f = new LongField("id", this.id, Field.Store.YES);
	newDoc.add(f);

	return newDoc;

    }

    @Override
    public String getContent() {
	return this.content;
    }

    @Override
    public int getId() {
	return this.id;
    }

    @Override
    public List<Integer> getReferences() {
	return this.references;
    }

    @Override
    public void setDocBoost(float boost) {
	this.boost = boost;
    }

}
