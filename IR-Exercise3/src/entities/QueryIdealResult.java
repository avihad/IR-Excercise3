package entities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QueryIdealResult {

    private final int queryId;
    private final Map<Integer, Integer> docsIdToRank;
    private final Map<Integer, Integer> docsByRank;

    public QueryIdealResult(int queryId) {

	this.queryId = queryId;
	this.docsIdToRank = new HashMap<Integer, Integer>();
	this.docsByRank = new HashMap<Integer, Integer>();
    }

    public void addResult(int docId, int rank) {

	this.docsIdToRank.put(docId, rank);
	this.docsByRank.put(rank, docId);

    }

    private double calcDCG(List<Integer> docRank) {

	if (docRank.isEmpty()) {
	    return 0;
	}

	double DCG = docRank.get(0);
	for (int i = 1; i < docRank.size(); i++) {
	    DCG += docRank.get(i) / (Math.log(i + 1) / Math.log(2));
	}

	return DCG;
    }

    public double calcNDCG(List<Integer> docList) {
	List<Integer> docRank = new ArrayList<Integer>();
	for (Integer docId : docList) {
	    docRank.add(this.docsIdToRank.getOrDefault(docId, 0));
	}
	double DCG = calcDCG(docRank);
	Collections.sort(docRank);
	double IDCG = calcDCG(docRank);

	return IDCG != 0 ? DCG / IDCG : 0;
    }

    public int getDocByRank(int rank) {
	Integer result = this.docsByRank.get(rank);

	if (result == null) {
	    result = -1;
	}

	return result;
    }

    public int getDocRank(int docId) {

	return this.docsIdToRank.get(docId);
    }

    public int getSize() {
	return this.docsIdToRank.size();
    }

}
