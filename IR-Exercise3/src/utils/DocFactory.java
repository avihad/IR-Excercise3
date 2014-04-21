package utils;

import entities.EngineStrategy;
import entities.IRDoc;
import entities.BasicIRDoc;
import entities.ImprovedIRDoc;

public enum DocFactory {

    instance;

    private EngineStrategy strategy = EngineStrategy.Basic;

    public IRDoc create(int id, String content) {
	switch (this.strategy) {
	    case Improved:
		return ImprovedIRDoc.create(id, content);
	    case Basic:
	    default:
		return new BasicIRDoc(id, content);
	}
    }

    public EngineStrategy getStrategy() {
	return this.strategy;
    }

    public void setStrategy(EngineStrategy strategy) {
	this.strategy = strategy;
    }

}
