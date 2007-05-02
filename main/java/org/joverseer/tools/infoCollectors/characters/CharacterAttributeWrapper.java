package org.joverseer.tools.infoCollectors.characters;

import org.joverseer.support.infoSources.InfoSource;

public class CharacterAttributeWrapper implements Comparable {
	String attribute;
	InfoSource infoSource;
	Object value;
        Object totalValue;
	int turnNo;
	
	public CharacterAttributeWrapper(String attribute, Object value, int turnNo, InfoSource infoSource) {
		this.attribute = attribute;
		this.value = value;
		this.turnNo = turnNo;
		this.infoSource = infoSource;
	}
        
        public CharacterAttributeWrapper(String attribute, Object value, Object totalValue, int turnNo, InfoSource infoSource) {
                this.attribute = attribute;
                this.value = value;
                this.turnNo = turnNo;
                this.infoSource = infoSource;
                this.totalValue = totalValue;
        }
        
	
	public InfoSource getInfoSource() {
		return infoSource;
	}
	public void setInfoSource(InfoSource infoSource) {
		this.infoSource = infoSource;
	}
	public String getAttribute() {
		return attribute;
	}
	public void setAttribute(String attribute) {
		this.attribute = attribute;
	}
	public Object getValue() {
		return value;
	}
	public void setValue(Object value) {
		this.value = value;
	}
	public int getTurnNo() {
		return turnNo;
	}
	public void setTurnNo(int turnNo) {
		this.turnNo = turnNo;
	}

    
    public Object getTotalValue() {
        return totalValue;
    }

    
    public void setTotalValue(Object totalValue) {
        this.totalValue = totalValue;
    }

    public int compareTo(Object o) {
        if (o == null) return 1;
        CharacterAttributeWrapper caw = (CharacterAttributeWrapper)o;
        Object v1 = getTotalValue();
        Object v2 = caw.getTotalValue();
        if (v1 == null && v2 != null) return -1;
        if (v1 != null && v2 == null) return 1;
        if (v1 != null && v2 != null) {
            return ((Comparable)v1).compareTo(v2);
        }
        v1 = getValue();
        v2 = caw.getValue();
        if (v1 == null && v2 != null) return -1;
        if (v1 != null && v2 == null) return 1;
        if (v1 != null && v2 != null) {
            return ((Comparable)v1).compareTo(v2);
        }
        return 0;
    }

        
	
}
