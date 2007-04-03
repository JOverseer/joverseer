package org.joverseer.domain;

import java.io.Serializable;


public class ArmyElement implements Serializable {

    private static final long serialVersionUID = -5669242428421000642L;
    ArmyElementType armyElementType;
    int number;
    int training;
    int weapons;
    int armor;

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

    
    public int getArmor() {
        return armor;
    }

    
    public void setArmor(int armor) {
        this.armor = armor;
    }

    
    public int getTraining() {
        return training;
    }

    
    public void setTraining(int training) {
        this.training = training;
    }

    
    public int getWeapons() {
        return weapons;
    }

    
    public void setWeapons(int weapons) {
        this.weapons = weapons;
    }

    
}
