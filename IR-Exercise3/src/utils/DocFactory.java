package utils;

import entities.BasicIRDoc;
import entities.EngineStrategy;
import entities.IRDoc;
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
		return BasicIRDoc.create(id, content);
	}
    }

    public EngineStrategy getStrategy() {
	return this.strategy;
    }

    public void setStrategy(EngineStrategy strategy) {
	this.strategy = strategy;
    }

}
