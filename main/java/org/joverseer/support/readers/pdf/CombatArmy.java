package org.joverseer.support.readers.pdf;

import java.util.ArrayList;

import org.joverseer.support.Container;

/**
 * Stores information about an army participating in a combat.
 * 
 * @author Marios Skounakis
 */
public class CombatArmy {
	String nation;
	String commanderName;
	String commanderTitle;
	String losses;
	String morale;
	boolean survived;
	String commanderOutcome;
	Container<CombatArmyElement> regiments = new Container<CombatArmyElement>();
	ArrayList<String> attackedArmies = new ArrayList<String>();

	public String getCommanderName() {
		return this.commanderName;
	}

	public void setCommanderName(String commanderName) {
		this.commanderName = commanderName;
	}

	public String getLosses() {
		return this.losses;
	}

	public void setLosses(String losses) {
		this.losses = losses;
	}

	public String getNation() {
		return this.nation;
	}

	public void setNation(String nation) {
		this.nation = nation;
	}

	public boolean isSurvived() {
		return this.survived;
	}

	public void setSurvived(boolean survived) {
		this.survived = survived;
	}

	public Container<CombatArmyElement> getRegiments() {
		return this.regiments;
	}

	public void setRegiments(Container<CombatArmyElement> regiments) {
		this.regiments = regiments;
	}

	public String getMorale() {
		return this.morale;
	}

	public void setMorale(String morale) {
		this.morale = morale;
	}

	public String getCommanderTitle() {
		return this.commanderTitle;
	}

	public void setCommanderTitle(String commanderTitle) {
		this.commanderTitle = commanderTitle;
	}

	public String getCommanderOutcome() {
		return this.commanderOutcome;
	}

	public void setCommanderOutcome(String commanderOutcome) {
		this.commanderOutcome = commanderOutcome;
	}

	public ArrayList<String> getAttackedArmies() {
		return this.attackedArmies;
	}

	public void AddAttackedArmy(String armyCommander) {
		this.attackedArmies.add(armyCommander);
	}
}