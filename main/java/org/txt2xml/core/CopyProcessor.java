package org.txt2xml.core;

public class CopyProcessor extends Processor {
	int i = 0;
	
	@Override
	protected boolean findMatch() {
		i++;
		return i == 1;
	}

	protected CharSequence getMatchedText() {
		return chars;
	}

	protected CharSequence getRemainderText() {
		return chars;
	}

	protected void resetMatching() {
		i = 0;
	}
	

}
