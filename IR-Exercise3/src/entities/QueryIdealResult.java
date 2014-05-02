package entities;

import java.util.ArrayList;
import java.util.List;

import utils.Pair;

public class QueryIdealResult {

    private final int queryId;
    private final List<Pair<Integer, Integer>> docsIdToRank;

    public QueryIdealResult(int queryId) {

	this.queryId = queryId;
	this.docsIdToRank = new ArrayList<Pair<Integer, Integer>>();
    }

    public boolean addResult(int docId, int rank) {

	return this.docsIdToRank.add(Pair.of(docId, rank));
    }

}
