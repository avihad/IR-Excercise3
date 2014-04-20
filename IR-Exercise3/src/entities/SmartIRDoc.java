package entities;

import java.util.Collections;
import java.util.List;

public class SmartIRDoc extends SimpleIRDoc {

    private final List<Integer> references;

    public SmartIRDoc(int docId, String content, List<Integer> references) {
	super(docId, content);
	this.references = Collections.unmodifiableList(references);

    }

    public List<Integer> getReferences() {
	return this.references;
    }

}
