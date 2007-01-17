package org.joverseer.domain;

import java.io.Serializable;


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
}
