package org.joverseer.support.readers.pdf;

import org.joverseer.domain.Army;
import org.joverseer.domain.ArmyElementType;


public class ArmyWrapper {
    String commander;
    String type;
    int food;
    int warships;
    int transports;
    int warMachines;
    
    public String getCommander() {
        return commander;
    }
    
    public void setCommander(String commander) {
        this.commander = commander;
    }
    
    public int getFood() {
        return food;
    }
    
    public void setFood(int food) {
        this.food = food;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    
    
    
    public int getTransports() {
        return transports;
    }

    
    public void setTransports(int transports) {
        this.transports = transports;
    }

    
    public int getWarMachines() {
        return warMachines;
    }

    
    public void setWarMachines(int warMachines) {
        this.warMachines = warMachines;
    }

    
    public int getWarships() {
        return warships;
    }

    
    public void setWarships(int warships) {
        this.warships = warships;
    }

    public void updateArmy(Army army) {
        army.setFood(food);
        army.setNavy(type.toUpperCase().equals("NAVY"));
        army.setElement(ArmyElementType.Warships, getWarships());
        army.setElement(ArmyElementType.Transports, getTransports());
        army.setElement(ArmyElementType.WarMachimes, getWarMachines());
    }
}
