package org.joverseer.ui.domain;

import org.joverseer.domain.IHasMapLocation;

/**
 * Wraps information for the results in the Track Char list view
 * 
 * @author Marios Skounakis
 */

//TODO Should implement IHasTurnNumber?
public class TrackCharacterInfo implements IHasMapLocation {
    int turnNo;
    String info;
    int hexNo;
    
    public String getInfo() {
        return info;
    }
    
    public void setInfo(String info) {
        this.info = info;
    }
    
    public int getTurnNo() {
        return turnNo;
    }
    
    public void setTurnNo(int turnNo) {
        this.turnNo = turnNo;
    }

    
    public int getHexNo() {
        return hexNo;
    }

    
    public void setHexNo(int hexNo) {
        this.hexNo = hexNo;
    }

    public int getX() {
        return getHexNo() / 100;
    }

    public int getY() {
        return getHexNo() % 100;
    }
    
    
}
