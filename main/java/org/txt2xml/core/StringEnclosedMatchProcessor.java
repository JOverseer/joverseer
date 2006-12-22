package org.txt2xml.core;

public class StringEnclosedMatchProcessor extends Processor {

    String startString;
    String endString;
    int matchStart = 0;
    int matchEnd = 0;
    boolean includeStart = true;
    boolean includeEnd = true;
    String currentEndString = null;

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

    public String[] getEndStrings() {
        return endString.split("\\|");
    }


    protected boolean findMatch() {
        assert (chars != null);// : "Null text but asked to findMatch!";
        String str = chars.toString();
        int currentStart = matchStart;
        matchStart = str.indexOf(startString, matchEnd);
        if (matchStart == -1)
            return false;
        String[] endStrings = getEndStrings();
        for (String endString : endStrings) {
            if (endString.equals("$")) {
                matchEnd = str.length() - 1;
            } else {
                matchEnd = str.indexOf(endString, matchStart + startString.length());
            }
            currentEndString = endString;
            if (matchEnd > -1) {
                return true;
            }
        }
        matchEnd = currentStart;
        return false;
    }

    protected CharSequence getMatchedText() {
        int matchStart = this.matchStart;
        int matchEnd = this.matchEnd;
        if (!includeStart) {
            matchStart = matchStart + startString.length();
        }
        if (!includeEnd) {
            matchEnd = matchEnd - currentEndString.length();
        }
        return chars.subSequence(matchStart, matchEnd);
    }

    protected CharSequence getRemainderText() {
        return chars.subSequence(matchEnd, chars.length());
    }

    
    public boolean isIncludeEnd() {
        return includeEnd;
    }

    
    public void setIncludeEnd(boolean includeEnd) {
        this.includeEnd = includeEnd;
    }

    
    public boolean isIncludeStart() {
        return includeStart;
    }

    
    public void setIncludeStart(boolean includeStart) {
        this.includeStart = includeStart;
    }


    
}
