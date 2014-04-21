package entities;

public class BasicIRDoc implements IRDoc {
    private final int    id;
    private final String content;

    public BasicIRDoc(int docId, String content) {
	this.id = docId;
	this.content = content;
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
