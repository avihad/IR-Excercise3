package entities;

import java.util.Comparator;

public class SearchResultComparator implements Comparator<SearchResult> {

    @Override
    public int compare(SearchResult o1, SearchResult o2) {
	if (o1 == null) {
	    if (o2 == null) {
		return 0;
	    } else {
		return -1;
	    }
	} else if (o2 == null) {
	    return 1;
	}

	int compResult = Double.compare(o1.getScore(), o2.getScore());
	return compResult != 0 ? compResult : Integer.compare(o1.getDocId(), o2.getDocId());
    }

}
