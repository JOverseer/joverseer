package org.joverseer.support.readers.pdf;

import org.joverseer.domain.Character;
import org.joverseer.domain.InformationSourceEnum;


public class HostageWrapper {
    String name;
    String nation;
    int hexNo;
    
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
    
    public String getNation() {
        return nation;
    }
    
    public void setNation(String nation) {
        this.nation = nation;
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
