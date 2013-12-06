package org.txt2xml.core;

public class CopyProcessor extends Processor {
	int i = 0;
	
	@Override
	protected boolean findMatch() {
		this.i++;
		return this.i == 1;
	}

	@Override
	protected CharSequence getMatchedText() {
		return this.chars;
	}

	@Override
	protected CharSequence getRemainderText() {
		return this.chars;
	}

	@Override
	protected void resetMatching() {
		this.i = 0;
	}
	

}
