package org.joverseer.domain;

import java.io.Serializable;

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
        return type;
    }
    
    public int foodConsumption() {
        if (type.equals("HC") || type.equals("LC")) return 2;
        if (type.equals("HI") || type.equals("LI") || type.equals("AR") || type.equals("MA")) return 1;
        return 0;
    }
    
    public boolean isCavalry() {
        return type.equals("HC") || type.equals("LC");
    }
    
    public boolean isTroop() {
    	return type.equals("HC") || type.equals("LC") || type.equals("HI") || type.equals("LI") || type.equals("AR") || type.equals("MA");
    }
    
    public boolean isInfantry() {
    	return type.equals("HI") || type.equals("LI") || type.equals("AR") || type.equals("MA");
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
}
