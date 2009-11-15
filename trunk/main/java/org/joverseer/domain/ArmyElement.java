package org.joverseer.domain;

import java.io.Serializable;

/**
 * Stores information about army elements (i.e. HC, LC, etc). For each element,
 * the number of men, training, weapons and armor are stored.
 * 
 * Ships and War Machines are also considered elements.
 * 
 * @author Marios Skounakis
 *
 */
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

    public int getMaintentance() {
    	int cost = 0;
    	if (getArmyElementType().equals(ArmyElementType.HeavyCavalry)) {
    		cost = 6;
    	} else if (getArmyElementType().equals(ArmyElementType.LightCavalry)) {
    		cost = 3;
    	} else if (getArmyElementType().equals(ArmyElementType.HeavyInfantry)) {
    		cost = 4;
    	} else if (getArmyElementType().equals(ArmyElementType.LightInfantry)) {
    		cost = 2;
    	} else if (getArmyElementType().equals(ArmyElementType.Archers)) {
    		cost = 2;
    	} else if (getArmyElementType().equals(ArmyElementType.MenAtArms)) {
    		cost = 1;
    	} else if (getArmyElementType().equals(ArmyElementType.Warships)) {
    		cost = 50;
    	}else if (getArmyElementType().equals(ArmyElementType.Transports)) {
    		cost = 50;
    	}
    	return getNumber() * cost;
    }
}
