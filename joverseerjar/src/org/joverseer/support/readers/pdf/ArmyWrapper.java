package org.joverseer.support.readers.pdf;

import org.joverseer.domain.Army;
import org.joverseer.domain.ArmyElement;
import org.joverseer.domain.ArmyElementType;
import org.joverseer.support.Container;

/**
 * Stores information about armies
 * 
 * @author Marios Skounakis
 */

public class ArmyWrapper {
	String commander;
	String type;
	int food;
	int warships;
	int transports;
	int warMachines;
	String morale;
	String climate;
	int hexNo;
	Container<ArmyElementWrapper> elements = new Container<ArmyElementWrapper>();

	public String getCommander() {
		return this.commander;
	}

	public void setCommander(String commander) {
		this.commander = commander;
	}

	public int getFood() {
		return this.food;
	}

	public void setFood(int food) {
		this.food = food;
	}

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getClimate() {
		return this.climate;
	}

	public void setClimate(String climate) {
		this.climate = climate;
	}

	public int getHexNo() {
		return this.hexNo;
	}

	public void setHexNo(int hexNo) {
		this.hexNo = hexNo;
	}

	public String getMorale() {
		return this.morale;
	}

	public void setMorale(String morale) {
		this.morale = morale;
	}

	public int getTransports() {
		return this.transports;
	}

	public void setTransports(int transports) {
		this.transports = transports;
	}

	public int getWarMachines() {
		return this.warMachines;
	}

	public void setWarMachines(int warMachines) {
		this.warMachines = warMachines;
	}

	public int getWarships() {
		return this.warships;
	}

	public void setWarships(int warships) {
		this.warships = warships;
	}

	public void updateArmy(Army army) {
		army.setFood(this.food);
		try {
			army.setMorale(Integer.parseInt(this.morale));
		} catch (Exception exc) {
		}
		;
		army.setNavy(this.type.toUpperCase().equals("NAVY"));
		army.setElement(ArmyElementType.Warships, getWarships());
		army.setElement(ArmyElementType.Transports, getTransports());
		army.setElement(ArmyElementType.WarMachimes, getWarMachines());
		for (ArmyElementWrapper aew : getArmyElements().getItems()) {
			ArmyElementType t = getArmyElementType(aew.getType());
			for (ArmyElement ae : army.getElements()) {
				if (ae.getArmyElementType() == t) {
					ae.setTraining(aew.getTraining());
					ae.setWeapons(aew.getWeapons());
					ae.setArmor(aew.getArmor());
				}
			}
		}
	}

	private ArmyElementType getArmyElementType(String type1) {
		type1 = type1.trim();
		if (type1.startsWith("Heavy Cavalry"))
			return ArmyElementType.HeavyCavalry;
		if (type1.startsWith("Light Cavalry"))
			return ArmyElementType.LightCavalry;
		if (type1.startsWith("Heavy Infantry"))
			return ArmyElementType.HeavyInfantry;
		if (type1.startsWith("Light Infantry"))
			return ArmyElementType.LightInfantry;
		if (type1.startsWith("Archers"))
			return ArmyElementType.Archers;
		if (type1.startsWith("Men at Arms") || type1.startsWith("Men-at-Arms"))
			return ArmyElementType.MenAtArms;
		return null;
	}

	public Container<ArmyElementWrapper> getArmyElements() {
		return this.elements;
	}

	public void setArmyElements(Container elements) {
		this.elements = elements;
	}

}
