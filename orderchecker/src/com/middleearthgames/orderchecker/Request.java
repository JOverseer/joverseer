package com.middleearthgames.orderchecker;

public class Request
{
	private String message;
	private boolean satisfied;
	public String getMessage() { return this.message;}
	public void setMessage(String value) { this.message = value;}
	public Boolean getSatisfied() { return this.satisfied; }
	public void setSatisfied(boolean value) { this.satisfied = value; }
	public Request(String message,boolean satisfied)
	{
		this.message = message;
		this.satisfied = satisfied;
	}
}