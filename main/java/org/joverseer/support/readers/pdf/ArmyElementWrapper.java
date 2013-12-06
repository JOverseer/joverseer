package org.joverseer.support.readers.pdf;

/**
 * Stores information about army elements
 * 
 * @author Marios Skounakis
 */
public class ArmyElementWrapper {
    String type;
    int number;
    int weapons;
    int armor;
    int training;
    String fluff;
    
    public int getArmor() {
        return this.armor;
    }
    
    public void setArmor(int armor) {
        this.armor = armor;
    }
    
    public String getFluff() {
        return this.fluff;
    }
    
    public void setFluff(String fluff) {
        this.fluff = fluff;
    }
    
    public int getNumber() {
        return this.number;
    }
    
    public void setNumber(int number) {
        this.number = number;
    }
    
    public int getTraining() {
        return this.training;
    }
    
    public void setTraining(int training) {
        this.training = training;
    }
    
    public String getType() {
        return this.type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public int getWeapons() {
        return this.weapons;
    }
    
    public void setWeapons(int weapons) {
        this.weapons = weapons;
    }
    
}
