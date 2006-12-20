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
}
