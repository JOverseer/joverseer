package org.joverseer.domain;

import java.io.Serializable;


public class Encounter implements IHasMapLocation, Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = -117862763508411685L;
    String character;
    int hexNo;
    String description;
    
    public String getCharacter() {
        return character;
    }
    
    public void setCharacter(String character) {
        this.character = character;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
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
