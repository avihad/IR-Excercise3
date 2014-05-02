package searchengine;

import java.util.List;

import entities.IRDoc;
import entities.SearchResult;

public interface ISearchEngine {
    Boolean index(List<IRDoc> documents);

    List<SearchResult> search(String query);
    
    void setStopwords(List<String> stopwords);
}
