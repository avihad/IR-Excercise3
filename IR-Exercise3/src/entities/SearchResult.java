package entities;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SearchResult {
    public static List<Integer> extractIds(Map.Entry<Integer, List<SearchResult>> queryResult) {
	List<Integer> docsIds = new ArrayList<Integer>();

	for (SearchResult searchResult : queryResult.getValue()) {
	    docsIds.add(searchResult.getDocId());
	}

	return docsIds;
    }

    private final int docId;

    private final float score;

    public SearchResult(int docId, float score) {
	this.docId = docId;
	this.score = score;
    }

    public int getDocId() {
	return this.docId;
    }

    public float getScore() {
	return this.score;
    }
}
