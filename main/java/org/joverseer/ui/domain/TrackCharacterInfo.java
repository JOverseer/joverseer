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
        return this.info;
    }
    
    public void setInfo(String info) {
        this.info = info;
    }
    
    public int getTurnNo() {
        return this.turnNo;
    }
    
    public void setTurnNo(int turnNo) {
        this.turnNo = turnNo;
    }

    
    public int getHexNo() {
        return this.hexNo;
    }

    
    public void setHexNo(int hexNo) {
        this.hexNo = hexNo;
    }

    @Override
	public int getX() {
        return getHexNo() / 100;
    }

    @Override
	public int getY() {
        return getHexNo() % 100;
    }
    
    
}
