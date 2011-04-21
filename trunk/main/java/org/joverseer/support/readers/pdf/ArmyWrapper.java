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

	public String getClimate() {
		return climate;
	}

	public void setClimate(String climate) {
		this.climate = climate;
	}

	public int getHexNo() {
		return hexNo;
	}

	public void setHexNo(int hexNo) {
		this.hexNo = hexNo;
	}

	public String getMorale() {
		return morale;
	}

	public void setMorale(String morale) {
		this.morale = morale;
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
		try {
			army.setMorale(Integer.parseInt(morale));
		} catch (Exception exc) {
		}
		;
		army.setNavy(type.toUpperCase().equals("NAVY"));
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

	private ArmyElementType getArmyElementType(String type) {
		type = type.trim();
		if (type.startsWith("Heavy Cavalry"))
			return ArmyElementType.HeavyCavalry;
		if (type.startsWith("Light Cavalry"))
			return ArmyElementType.LightCavalry;
		if (type.startsWith("Heavy Infantry"))
			return ArmyElementType.HeavyInfantry;
		if (type.startsWith("Light Infantry"))
			return ArmyElementType.LightInfantry;
		if (type.startsWith("Archers"))
			return ArmyElementType.Archers;
		if (type.startsWith("Men at Arms") || type.startsWith("Men-at-Arms"))
			return ArmyElementType.MenAtArms;
		return null;
	}

	public Container<ArmyElementWrapper> getArmyElements() {
		return elements;
	}

	public void setArmyElements(Container elements) {
		this.elements = elements;
	}

}
