package org.joverseer.domain;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: mskounak
 * Date: 20 Οκτ 2006
 * Time: 11:01:13 μμ
 * To change this template use File | Settings | File Templates.
 */
public class ArmyElement implements Serializable {
    ArmyElementType armyElementType;
    int number;

    public ArmyElement(ArmyElementType armyElementType, int number) {
        this.armyElementType = armyElementType;
        this.number = number;
    }

    public ArmyElement() {
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public ArmyElementType getArmyElementType() {
        return armyElementType;
    }

    public void setArmyElementType(ArmyElementType armyElementType) {
        this.armyElementType = armyElementType;
    }

    public String getDescription() {
        return String.valueOf(getNumber()) + getArmyElementType().getType();
    }

}
