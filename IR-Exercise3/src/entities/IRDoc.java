package entities;

import java.util.List;

import org.apache.lucene.document.Document;

public interface IRDoc {

    public Document createDocument();

    public String getContent();

    public int getId();
    
    public List<Integer> getReferences();
    
    public void setDocBoost(float boost);
    
    public IRDoc Clone();
}
