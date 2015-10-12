package org.joverseer.support.readers.pdf;

import org.joverseer.domain.Character;
import org.joverseer.domain.InformationSourceEnum;

/**
 * Holds information about hostages
 * 
 * @author Marios Skounakis
 */
public class HostageWrapper {
    String name;
    String nation;
    String owner;
    int hexNo;
    
    public int getHexNo() {
        return this.hexNo;
    }
    
    public void setHexNo(int hexNo) {
        this.hexNo = hexNo;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getNation() {
        return this.nation;
    }
    
    public void setNation(String nation) {
        this.nation = nation;
    }
    
    public String getOwner() {
        return this.owner;
    }

    
    public void setOwner(String owner) {
        this.owner = owner;
    }

    public Character getCharacter() {
        Character c = new Character();
        c.setName(getName());
        c.setId(Character.getIdFromName(getName()));
        c.setHexNo(getHexNo());
        c.setNationNo(0);
        c.setHostage(true);
        c.setInformationSource(InformationSourceEnum.limited);
        return c;
    }
}
