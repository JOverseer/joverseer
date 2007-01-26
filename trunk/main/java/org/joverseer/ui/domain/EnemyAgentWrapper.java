package org.joverseer.ui.domain;

import org.joverseer.domain.IHasMapLocation;


public class EnemyAgentWrapper implements IHasMapLocation {
    String name;
    int hexNo;
    int turnNo;
    String reportedTurns = "";
    boolean startChar;
    
    public int getHexNo() {
        return hexNo;
    }
    
    public void setHexNo(int hexNo) {
        this.hexNo = hexNo;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getReportedTurns() {
        return reportedTurns;
    }
    
    public void setReportedTurns(String reportedTurns) {
        this.reportedTurns = reportedTurns;
    }
    
    public int getTurnNo() {
        return turnNo;
    }
    
    public void setTurnNo(int turnNo) {
        this.turnNo = turnNo;
    }
    public int getX() {
        return hexNo / 100;
    }
    public int getY() {
        return hexNo % 100;
    }
    
    public void addReport(String rep) {
        reportedTurns += (reportedTurns.equals("") ? "" : ", ") + rep;
    }

    
    public boolean getStartChar() {
        return startChar;
    }

    
    public void setStartChar(boolean startChar) {
        this.startChar = startChar;
    }

    
    
}
