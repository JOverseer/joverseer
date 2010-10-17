package org.joverseer.engine;

public enum OrderDifficultyEnum {
	Easy (1),
	Average (2),
	Hard (3);
	
	int value;

	private OrderDifficultyEnum(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}
	
	
	
}
