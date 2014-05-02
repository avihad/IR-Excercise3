package entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import utils.Pair;

public class QueryIdealResult {

    private final int queryId;
    private final List<Pair<Integer, Integer>> docsIdToRank;
    private final Map<Integer, Integer> docsByRank;
    
    public QueryIdealResult(int queryId) {

	this.queryId = queryId;
	this.docsIdToRank = new ArrayList<Pair<Integer, Integer>>();
	this.docsByRank = new HashMap<Integer, Integer>();
    }
    
    public int numOfResults()
    {
    	return this.docsIdToRank.size();
    }

    public boolean addResult(int docId, int rank) {

	boolean result = this.docsIdToRank.add(Pair.of(docId, rank));
	
	if(result)
	{
		docsByRank.put(rank, docId);
	}
	
	return result;
    }
    
    public int getDocByRank(int rank)
    {
    	Integer result = docsByRank.get(rank);
    	
    	if(result == null)
    	{
    		result = -1;
    	}
    	
    	return result;
    }
    
    public int getDocRank(int docId)
    {
    	int result = -1;
    	
    	for(Pair<Integer, Integer> pair : docsIdToRank)
    	{
    		if(pair.first == docId)
    		{
    			result = pair.second;
    			break;
    		}
    	}
    	
    	return result;
    }

}
