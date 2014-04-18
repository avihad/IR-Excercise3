package LuceneWrapper;

public interface ILuceneWrapper {
	Integer[] Index(MyDoc[] documents);
	SearchResult[] Search(String query);
}
