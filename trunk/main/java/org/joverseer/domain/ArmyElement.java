package org.joverseer.domain;

import java.io.Serializable;


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
