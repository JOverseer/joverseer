package org.joverseer.support.readers.pdf;


public class ArmyElementWrapper {
    String type;
    int number;
    int weapons;
    int armor;
    int training;
    String fluff;
    
    public int getArmor() {
        return armor;
    }
    
    public void setArmor(int armor) {
        this.armor = armor;
    }
    
    public String getFluff() {
        return fluff;
    }
    
    public void setFluff(String fluff) {
        this.fluff = fluff;
    }
    
    public int getNumber() {
        return number;
    }
    
    public void setNumber(int number) {
        this.number = number;
    }
    
    public int getTraining() {
        return training;
    }
    
    public void setTraining(int training) {
        this.training = training;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public int getWeapons() {
        return weapons;
    }
    
    public void setWeapons(int weapons) {
        this.weapons = weapons;
    }
    
}
