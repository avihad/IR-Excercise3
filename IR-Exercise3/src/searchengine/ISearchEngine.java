package searchengine;

import java.util.List;

import entities.IRDoc;
import entities.SearchResult;

/**
 * Interface for our Search engine
 * */
public interface ISearchEngine {
    Boolean index(List<IRDoc> documents);

    List<SearchResult> search(IRDoc doc);

    void setStopwords(List<String> stopwords);
}
