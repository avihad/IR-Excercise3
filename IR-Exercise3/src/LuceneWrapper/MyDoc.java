package LuceneWrapper;

public class MyDoc {
    private final int    id;
    private final String content;

    public MyDoc(int docId, String content) {
	this.id = docId;
	this.content = content;
    }

    public String getContent() {
	return this.content;
    }

    public int getId() {
	return this.id;
    }

}
