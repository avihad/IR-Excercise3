package entities;

public enum EngineStrategy {
    Basic("basic"), Improved("improved");

    private String value;

    EngineStrategy(String value) {
	this.value = value;
    }

}
