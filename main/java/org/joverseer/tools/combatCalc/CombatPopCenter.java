package org.joverseer.tools.combatCalc;

import org.joverseer.domain.FortificationSizeEnum;
import org.joverseer.domain.PopulationCenter;
import org.joverseer.domain.PopulationCenterSizeEnum;


public class CombatPopCenter {
    String name;
    int hexNo;
    PopulationCenterSizeEnum size;
    FortificationSizeEnum fort;
    int loyalty;
    
    public CombatPopCenter() {
        size = PopulationCenterSizeEnum.town;
        fort = FortificationSizeEnum.none;
        loyalty = 50;
    }
    
    public CombatPopCenter(PopulationCenter pc) {
        name = pc.getName();
        hexNo = pc.getHexNo();
        size = pc.getSize();
        fort = pc.getFortification();
        loyalty = pc.getLoyalty();
    }
    
    public FortificationSizeEnum getFort() {
        return fort;
    }
    
    public void setFort(FortificationSizeEnum fort) {
        this.fort = fort;
    }
    
    public int getHexNo() {
        return hexNo;
    }
    
    public void setHexNo(int hexNo) {
        this.hexNo = hexNo;
    }
    
    public int getLoyalty() {
        return loyalty;
    }
    
    public void setLoyalty(int loyalty) {
        this.loyalty = loyalty;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public PopulationCenterSizeEnum getSize() {
        return size;
    }
    
    public void setSize(PopulationCenterSizeEnum size) {
        this.size = size;
    }
    
    
}
