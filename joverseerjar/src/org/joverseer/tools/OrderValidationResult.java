package org.joverseer.tools;

public class OrderValidationResult {
	public static int INFO = 1;
	public static int WARNING = 2;
	public static int ERROR = 3;
	
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
		return this.level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	public String getMessage() {
		return this.message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
	
}
