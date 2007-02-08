package org.joverseer.ui.domain;

import org.joverseer.domain.IHasMapLocation;


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
