package org.joverseer.tools.combatCalc;

import java.io.Serializable;

import org.joverseer.domain.FortificationSizeEnum;
import org.joverseer.domain.PopulationCenter;
import org.joverseer.domain.PopulationCenterSizeEnum;

/**
 * Holds all the information pertinent to a population center within a land combat such as
 * - name
 * - loyalty
 * - size
 * - fortifications
 * - nation
 * 
 * @author Marios Skounakis
 *
 */
public class CombatPopCenter implements Serializable {
    private static final long serialVersionUID = -1043824019785601335L;
	String name;
    int hexNo;
    PopulationCenterSizeEnum size;
    FortificationSizeEnum fort;
    int loyalty;
    Integer nationNo;
    boolean captured = false;
    
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

    
    public Integer getNationNo() {
        return nationNo;
    }

    
    public void setNationNo(Integer nationNo) {
        this.nationNo = nationNo;
    }

    
    public boolean isCaptured() {
        return captured;
    }

    
    public void setCaptured(boolean captured) {
        this.captured = captured;
    }
    
    
}
