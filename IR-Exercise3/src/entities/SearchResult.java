package entities;

public class SearchResult {
    private final String docId;
    private final float  score;

    public SearchResult(String docId, float score) {
	this.docId = docId;
	this.score = score;
    }

    public String getDocId() {
	return this.docId;
    }

    public float getScore() {
	return this.score;
    }
}
