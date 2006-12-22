package org.joverseer.support.readers.pdf;

import java.util.ArrayList;


public class CombatWrapper {
    String narration;
    int hexNo;
    ArrayList<CombatArmy> armies = new ArrayList<CombatArmy>(); 
    
    
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

    private class CombatArmy {
        String nation;
        String commanderName;
        String losses;
        boolean survived;
        
        public String getCommanderName() {
            return commanderName;
        }
        
        public void setCommanderName(String commanderName) {
            this.commanderName = commanderName;
        }
        
        public String getLosses() {
            return losses;
        }
        
        public void setLosses(String losses) {
            this.losses = losses;
        }
        
        public String getNation() {
            return nation;
        }
        
        public void setNation(String nation) {
            this.nation = nation;
        }
        
        public boolean isSurvived() {
            return survived;
        }
        
        public void setSurvived(boolean survived) {
            this.survived = survived;
        }
        
        
    }
}
