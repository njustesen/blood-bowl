package models;

public enum PassRange {
	QUICK_PASS(-1, "quick pass"), SHORT_PASS(0, "short pass"), LONG_PASS(1, "long pass"), LONG_BOMB(2, "long bomb"), OUT_OF_RANGE(0, "out of range");
	
	private final int modifier;
	private final String name;
	
	PassRange(int modifier, String name) {
		this.modifier = modifier;
		this.name = name;
    }
	
	public int getModifier() { return modifier; }
	public String getName() { return name; }
	
}
