package LuceneWrapper;

import java.util.List;

public interface ILuceneWrapper {
    Boolean index(List<MyDoc> documents);

    List<SearchResult> search(String query);
}
