package org.joverseer.tools.combatCalc;

import java.util.ArrayList;

import org.joverseer.domain.Army;
import org.joverseer.domain.Character;
import org.joverseer.domain.ArmyElement;
import org.joverseer.game.Game;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.support.GameHolder;


public class CombatArmy {
    String commander;
    int nationNo;
    ArrayList<ArmyElement> elements = new ArrayList<ArmyElement>();
    int morale;
    int commandRank;
    TacticEnum tactic;
    
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
    
    public CombatArmy() {
    	
    }
    
    public CombatArmy(Army a) {
    	Game g = GameHolder.instance().getGame();
    	for (ArmyElement ae : a.getElements()) {
    		getElements().add(ae);
    	}
    	Character c = (Character)g.getTurn().getContainer(TurnElementsEnum.Character).findFirstByProperty("name", a.getCommanderName());
    	if (c != null) {
    		setCommandRank(c.getCommandTotal());
    	} else {
    		setCommandRank(50);
    	}
    	setMorale(a.getMorale());
    	setTactic(TacticEnum.Standard);
    }
}
