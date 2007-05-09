package org.joverseer.tools.combatCalc;

import java.util.ArrayList;

import org.joverseer.domain.Army;
import org.joverseer.domain.ArmyElement;
import org.joverseer.domain.ArmyElementType;
import org.joverseer.domain.Character;
import org.joverseer.game.Game;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.metadata.domain.Nation;
import org.joverseer.support.GameHolder;
import org.joverseer.support.NationMap;


public class CombatArmy {

    String commander;
    int nationNo;
    ArrayList<ArmyElement> elements = new ArrayList<ArmyElement>();
    int morale;
    int commandRank;
    TacticEnum tactic;
    double losses;


    int offensiveAddOns;
    int defensiveAddOns;

    public String getCommander() {
        return commander;
    }

    public void setCommander(String commander) {
        this.commander = commander;
    }

    public int getCommandRank() {
        return commandRank;
    }

    public void setCommandRank(int commandRank) {
        this.commandRank = commandRank;
    }

    public int getDefensiveAddOns() {
        return defensiveAddOns;
    }

    public void setDefensiveAddOns(int defensiveAddOns) {
        this.defensiveAddOns = defensiveAddOns;
    }

    public ArrayList<ArmyElement> getElements() {
        return elements;
    }

    public void setElements(ArrayList<ArmyElement> elements) {
        this.elements = elements;
    }

    public int getMorale() {
        return morale;
    }

    public void setMorale(int morale) {
        this.morale = morale;
    }

    public int getNationNo() {
        return nationNo;
    }

    public void setNationNo(int nationNo) {
        this.nationNo = nationNo;
    }

    public int getOffensiveAddOns() {
        return offensiveAddOns;
    }

    public void setOffensiveAddOns(int offensiveAddOns) {
        this.offensiveAddOns = offensiveAddOns;
    }

    public TacticEnum getTactic() {
        return tactic;
    }

    public void setTactic(TacticEnum tactic) {
        this.tactic = tactic;
    }
    
    

    
    public double getLosses() {
        return losses;
    }

    
    public void setLosses(double losses) {
        this.losses = losses;
    }
    
    public ArmyElement getArmyElement(ArmyElementType aet) {
        for (ArmyElement ae : getElements()) {
            if (ae.getArmyElementType() == aet) return ae;
        }
        return null;
    }
    
    public ArmyElement getHC() {
        return getArmyElement(ArmyElementType.HeavyCavalry); 
    }

    public ArmyElement getLC() {
        return getArmyElement(ArmyElementType.LightCavalry); 
    }

    public ArmyElement getHI() {
        return getArmyElement(ArmyElementType.HeavyInfantry); 
    }

    public ArmyElement getLI() {
        return getArmyElement(ArmyElementType.LightInfantry); 
    }

    public ArmyElement getAR() {
        return getArmyElement(ArmyElementType.Archers); 
    }

    public ArmyElement getMA() {
        return getArmyElement(ArmyElementType.MenAtArms); 
    }

    public ArmyElement getWM() {
        return getArmyElement(ArmyElementType.WarMachimes); 
    }

    public Nation getNation() {
        return NationMap.getNationFromNo(getNationNo());
    }

    public void setNation(Nation nation) {
        setNationNo(nation.getNumber());
    }

    
    public CombatArmy() {
        for (ArmyElementType aet : ArmyElementType.values()) {
            ArmyElement ae = new ArmyElement(aet, 0);
            getElements().add(ae);
        }
        tactic = TacticEnum.Standard;
    }

    public CombatArmy(Army a) {
        Game g = GameHolder.instance().getGame();
        for (ArmyElementType aet : ArmyElementType.values()) {
            ArmyElement ae = a.getElement(aet);
            if (ae == null) {
                ae = new ArmyElement(aet, 0);
            }
            ArmyElement nae = new ArmyElement(ae.getArmyElementType(), ae.getNumber());
            nae.setWeapons(ae.getWeapons());
            nae.setTraining(ae.getTraining());
            nae.setArmor(ae.getArmor());
            getElements().add(nae);
        }
        
        Character c = (Character) g.getTurn().getContainer(TurnElementsEnum.Character).findFirstByProperty("name",
                a.getCommanderName());
        if (c != null) {
            setCommandRank(c.getCommandTotal());
        } else {
            setCommandRank(50);
        }
        setNationNo(a.getNationNo());
        setCommander(a.getCommanderName());
        setMorale(a.getMorale());
        setTactic(TacticEnum.Standard);
    }
}
