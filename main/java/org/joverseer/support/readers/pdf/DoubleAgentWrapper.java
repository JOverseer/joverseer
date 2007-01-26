package org.joverseer.support.readers.pdf;

import org.joverseer.domain.Character;
import org.joverseer.domain.InformationSourceEnum;

public class DoubleAgentWrapper {
    String name;
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
    
    public Character getCharacter() {
        Character c = new Character();
        c.setName(getName());
        c.setId(Character.getIdFromName(getName()));
        c.setHexNo(String.valueOf(getHexNo()));
        c.setNationNo(0);
        c.setInformationSource(InformationSourceEnum.limited);
        return c;
    }
}
