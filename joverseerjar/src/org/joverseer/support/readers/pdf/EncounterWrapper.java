package org.joverseer.support.readers.pdf;

import org.joverseer.domain.Encounter;

/**
 * Holds information about encounters
 * 
 * @author Marios Skounakis
 */
public class EncounterWrapper {
    String character;
    int hexNo;
    String description;
    
    public String getCharacter() {
        return this.character;
    }
    
    public void setCharacter(String character) {
        this.character = character;
    }
    
    public String getDescription() {
        return this.description;
    }
    
    public void setDescription(String description) {
        if (description == null || description.equals("")) return;
        this.description = description;
    }
    
    public int getHexNo() {
        return this.hexNo;
    }
    
    public void setHexNo(int hexNo) {
        this.hexNo = hexNo;
    }
    
    public Encounter getEncounter() {
        Encounter e = new Encounter();
        e.setCharacter(getCharacter());
        e.setHexNo(getHexNo());
        e.setDescription(getDescription());
        return e;
    }
}
