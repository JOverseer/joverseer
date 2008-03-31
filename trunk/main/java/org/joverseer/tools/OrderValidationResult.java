package org.joverseer.tools;

public class OrderValidationResult {
	public static int WARNING = 1;
	public static int ERROR = 2;
	
	int level;
	String message;
	
	public OrderValidationResult() {
		
	}
	
	
	
	public OrderValidationResult(int level, String message) {
		super();
		this.level = level;
		this.message = message;
	}



	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
	
}
