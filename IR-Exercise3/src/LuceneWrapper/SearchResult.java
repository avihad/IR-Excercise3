package LuceneWrapper;

public class SearchResult {
	public String _docId;
	public float _score;
	
	public SearchResult(String docId, float score)
	{
		this._docId = docId;
		this._score = score;
	}
}
