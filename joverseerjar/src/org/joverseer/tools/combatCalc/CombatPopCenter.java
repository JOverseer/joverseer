package org.joverseer.tools.combatCalc;

import java.io.Serializable;

import org.joverseer.domain.FortificationSizeEnum;
import org.joverseer.domain.PopulationCenter;
import org.joverseer.domain.PopulationCenterSizeEnum;
import org.joverseer.metadata.domain.Nation;
import org.joverseer.support.NationMap;

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
    int strengthOfAttackingArmies;
    
    public CombatPopCenter() {
        this.size = PopulationCenterSizeEnum.town;
        this.fort = FortificationSizeEnum.none;
        this.loyalty = 50;
    }
    
    public CombatPopCenter(PopulationCenter pc) {
        this.name = pc.getName();
        this.hexNo = pc.getHexNo();
        this.size = pc.getSize();
        this.fort = pc.getFortification();
        this.loyalty = pc.getLoyalty();
        this.nationNo = pc.getNationNo();
    }
    
    public FortificationSizeEnum getFort() {
        return this.fort;
    }
    
    public void setFort(FortificationSizeEnum fort) {
        this.fort = fort;
    }
    
    public int getHexNo() {
        return this.hexNo;
    }
    
    public void setHexNo(int hexNo) {
        this.hexNo = hexNo;
    }
    
    public int getLoyalty() {
        return this.loyalty;
    }
    
    public void setLoyalty(int loyalty) {
        this.loyalty = loyalty;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public PopulationCenterSizeEnum getSize() {
        return this.size;
    }
    
    public void setSize(PopulationCenterSizeEnum size) {
        this.size = size;
    }

    
    public Integer getNationNo() {
        return this.nationNo;
    }

    
    public void setNationNo(Integer nationNo) {
        this.nationNo = nationNo;
    }
    
    public Nation getNation() {
        return NationMap.getNationFromNo(getNationNo());
    }

    public void setNation(Nation nation) {
        setNationNo(nation.getNumber());
    }

    
    public boolean isCaptured() {
        return this.captured;
    }

    
    public void setCaptured(boolean captured) {
        this.captured = captured;
    }

    
    public int getStrengthOfAttackingArmies() {
        return this.strengthOfAttackingArmies;
    }

    
    public void setStrengthOfAttackingArmies(int strengthOfAttackingArmies) {
        this.strengthOfAttackingArmies = strengthOfAttackingArmies;
    }

    public String getCapturedStr() {
    	return isCaptured() ? "yes" : "no";
    }
    
    
}
