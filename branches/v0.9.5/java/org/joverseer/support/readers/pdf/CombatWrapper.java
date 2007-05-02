package org.joverseer.support.readers.pdf;

import java.util.ArrayList;

import org.joverseer.support.Container;


public class CombatWrapper {
    String narration;
    int hexNo;
    Container armies = new Container(); 
    
    
    public int getHexNo() {
        return hexNo;
    }
    
    public void setHexNo(int hexNo) {
        this.hexNo = hexNo;
    }
    
    public String getNarration() {
        return narration;
    }
    
    public void setNarration(String narration) {
        this.narration = narration;
    }
    
    
    
    
    public Container getArmies() {
        return armies;
    }

    
    public void setArmies(Container armies) {
        this.armies = armies;
    }

    public void parse() {
    }
    
    private static String getStringSegment(String string, String startString, String endString, boolean includeStart, boolean includeEnd) {
        int idx1 = string.indexOf(startString);
        if (idx1 == -1) return null;
        int idx2 = (endString != null ? string.indexOf(endString, idx1) + endString.length() : string.length());
        if (idx2 == 0) return null;
        if (!includeStart) {
            idx1 = idx1 + startString.length() + 1;
        }
        if (!includeEnd && endString != null) {
            idx2 = idx2 - endString.length();
        }
        return string.substring(idx1, idx2).trim();
    }

    
    
    
}
