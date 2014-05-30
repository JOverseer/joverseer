package org.joverseer.domain;

import java.io.Serializable;

import org.joverseer.ui.support.Messages;

/**
 * Enumeration for army element types
 * 
 * @author Marios Skounakis
 *
 */
public enum ArmyElementType implements Serializable {
    HeavyCavalry ("HC"),
    LightCavalry ("LC"),
    HeavyInfantry ("HI"),
    LightInfantry ("LI"),
    Archers ("AR"),
    MenAtArms ("MA"),
    WarMachimes ("WM"),
    Warships ("WA"),
    Transports ("TR");

    private String type;

    private ArmyElementType(String type) {
        this.type = type;
    }

    public String getType() {
        return this.type;
    }
    
    public int foodConsumption() {
        if (this.type.equals("HC") || this.type.equals("LC")) return 2;
        if (this.type.equals("HI") || this.type.equals("LI") || this.type.equals("AR") || this.type.equals("MA")) return 1;
        return 0;
    }
    
    public boolean isCavalry() {
        return this.type.equals("HC") || this.type.equals("LC");
    }
    
    public boolean isTroop() {
    	return this.type.equals("HC") || this.type.equals("LC") || this.type.equals("HI") || this.type.equals("LI") || this.type.equals("AR") || this.type.equals("MA");
    }
    
    public boolean isInfantry() {
    	return this.type.equals("HI") || this.type.equals("LI") || this.type.equals("AR") || this.type.equals("MA");
    }
    
    public double getRequiredTransportCapacity() {
    	if (!isTroop()) return 0;
    	if (isCavalry()) return 250d / 150d;
    	return 1;
    }
    
    public static ArmyElementType getFromString(String type) {
    	for (ArmyElementType aet : ArmyElementType.values()) {
    		if (aet.getType().equals(type)) return aet;
    	}
    	return null;
    }
    
    public int getSortOrder() {
    	int i = 0;
    	for (ArmyElementType aet : ArmyElementType.values()) {
    		if (this.equals(aet)) return i;
    		i++;
    	}
    	return i;
    }
    public String getLocalizedUC() {
    	return Messages.getString("ArmyElementType."+this.type);
    }
}
