package entities;

/**
 * Enum represent the strategy of the search engine
 * */
public enum EngineStrategy {
    Basic("basic"), Improved("improved");

    private String value;

    EngineStrategy(String value) {
	this.value = value;
    }

    public String getValue() {
	return this.value;
    }

}
