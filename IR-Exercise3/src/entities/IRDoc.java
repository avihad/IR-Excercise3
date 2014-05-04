package entities;

import java.util.List;

import org.apache.lucene.document.Document;

/**
 * Information retrieval doc interface
 * */
public interface IRDoc {

    public IRDoc Clone();

    public Document createDocument();

    public String getContent();

    public int getId();

    public List<Integer> getReferences();

    public void setDocBoost(float boost);
}
