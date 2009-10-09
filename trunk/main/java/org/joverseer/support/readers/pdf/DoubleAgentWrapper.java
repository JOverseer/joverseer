package org.joverseer.support.readers.pdf;

import org.joverseer.domain.Character;
import org.joverseer.domain.InformationSourceEnum;
import org.joverseer.metadata.domain.Nation;
import org.joverseer.support.GameHolder;

/**
 * Holds information about Double Agents
 * 
 * @author Marios Skounakis
 */
public class DoubleAgentWrapper {
    String name;
    int hexNo;
    String nation;
    String orders;
    
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
        Nation n = GameHolder.instance().getGame().getMetadata().getNationByName(getNation());
        c.setNationNo(n == null ? 0 : n.getNumber());
        c.setInformationSource(InformationSourceEnum.limited);
        c.setOrderResults(getOrders());
        return c;
    }

	public String getOrders() {
		return orders;
	}

	public void setOrders(String orders) {
		this.orders = orders;
	}
}
