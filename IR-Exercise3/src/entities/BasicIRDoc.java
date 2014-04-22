package entities;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.TextField;

public class BasicIRDoc implements IRDoc {
    public static IRDoc create(int docId, String content) {

	return new BasicIRDoc(docId, content);
    }

    private final int    id;

    private final String content;

    protected BasicIRDoc(int docId, String content) {
	this.id = docId;
	this.content = content;
    }

    @Override
    public Document createDocument() {
	Document newDoc = new Document();
	newDoc.add(new TextField("content", this.content, Field.Store.YES));
	newDoc.add(new LongField("id", this.id, Field.Store.YES));

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

}
