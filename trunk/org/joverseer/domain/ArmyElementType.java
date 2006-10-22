package org.joverseer.domain;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: mskounak
 * Date: 20 Οκτ 2006
 * Time: 11:01:33 μμ
 * To change this template use File | Settings | File Templates.
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
}
