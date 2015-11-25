package org.txt2xml.core;

public class StringEnclosedMatchProcessor extends Processor {

    String startString;
    String endString;
    int matchStart = 0;
    int matchEnd = 0;
    boolean includeStart = true;
    boolean includeEnd = true;
    boolean removeNewLines = false;
    String currentEndString = null;

    public String getEndString() {
        return this.endString;
    }

    public void setEndString(String endString) {
        this.endString = endString;
    }

    public String getStartString() {
        return this.startString;
    }


    public void setStartString(String startString) {
        this.startString = startString;
    }

    public String[] getEndStrings() {
        return this.endString.split("\\|");
    }
    
    public String[] getStartStrings() {
        return this.startString.split("\\|");
    }


    @SuppressWarnings("null")
	@Override
	protected boolean findMatch() {
        assert (this.chars != null);// : "Null text but asked to findMatch!";
        String str = this.chars.toString();
        if (this.removeNewLines) {
        	str = str.replace("\n", "").replace("\r", "");
        }
        int currentStart = this.matchStart;
        String[] startStrings = getStartStrings();
        String foundStartString = null;
        // find first match
        int idx = -1;
        for (String startString1 : startStrings) {
        	if (startString1.equals("^")) {
        		this.matchStart = this.matchEnd;
        	} else {
        		this.matchStart = str.indexOf(startString1, this.matchEnd);
        	}
            if (this.matchStart > -1 && (this.matchStart <= idx || idx == -1)) {
                idx = this.matchStart;
                foundStartString = startString1;
            }
        }
        if (idx == -1) {
            return false;
        }
        this.matchStart = idx;
        
        String[] endStrings = getEndStrings();
        idx = -1;
        for (String endString1 : endStrings) {
            if (endString1.equals("$")) {
                this.matchEnd = str.length() - 1;
            } else {
                this.matchEnd = str.indexOf(endString1, this.matchStart + foundStartString.length());
            }
            if (this.matchEnd > -1 && (this.matchEnd <= idx || idx == -1)) {
                idx = this.matchEnd;
                this.currentEndString = endString1;
            }
        }
        if (idx > -1) {
            this.matchEnd = idx;
            return true;
        }
        this.matchEnd = currentStart;
        return false;
    }

    @Override
	protected CharSequence getMatchedText() {
        int matchStart1 = this.matchStart;
        int matchEnd1 = this.matchEnd;
        if (!this.includeStart) {
            matchStart1 = matchStart1 + this.startString.length();
        }
        if (this.includeEnd) {
            if (!this.currentEndString.equals("$")) {
                matchEnd1 = matchEnd1 + this.currentEndString.length();
            }
        }
        return this.chars.subSequence(matchStart1, matchEnd1);
    }

    @Override
	protected CharSequence getRemainderText() {
        return this.chars.subSequence(this.matchEnd, this.chars.length());
    }

    
    public boolean isIncludeEnd() {
        return this.includeEnd;
    }

    
    public void setIncludeEnd(boolean includeEnd) {
        this.includeEnd = includeEnd;
    }

    
    public boolean isIncludeStart() {
        return this.includeStart;
    }

    
    public void setIncludeStart(boolean includeStart) {
        this.includeStart = includeStart;
    }

    @Override
	protected void resetMatching() {
        super.resetMatching();
        this.matchStart = 0;
        this.matchEnd = 0;
    }

	public boolean getRemoveNewLines() {
		return this.removeNewLines;
	}

	public void setRemoveNewLines(boolean removeNewLines) {
		this.removeNewLines = removeNewLines;
	}

    
    

    
}
