package org.txt2xml.core;

public class StringEnclosedMatchProcessor extends Processor {
	String startString;
	String endString;
	int matchStart = 0;
	int matchEnd = 0;
	
	public String getEndString() {
		return endString;
	}

	public void setEndString(String endString) {
		this.endString = endString;
	}

	public String getStartString() {
		return startString;
	}

	public void setStartString(String startString) {
		this.startString = startString;
	}

	protected boolean findMatch() {
		assert (chars != null);// : "Null text but asked to findMatch!";
		String str = chars.toString();
		matchStart = str.indexOf(startString, matchEnd);
		if (matchStart == -1) return false;
		matchEnd = str.indexOf(endString, matchStart);
		if (matchEnd == -1) return false;
		return true;
	}

	protected CharSequence getMatchedText() {
		return chars.subSequence(matchStart, matchEnd);
	}

	protected CharSequence getRemainderText() {
		return chars.subSequence(matchEnd, chars.length());
	}
	

}
