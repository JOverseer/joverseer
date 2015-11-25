package org.joverseer.support.readers.newXml;

import java.util.ArrayList;

import org.joverseer.domain.Army;
import org.joverseer.domain.ArmyElement;
import org.joverseer.domain.ArmyElementType;

public class ArmyWrapper {
	String commander;
	int food;
	int morale;
	int transports;
	int warships;
	int warmachines;
	String climate;
	ArrayList<ArmyRegimentWrapper> regiments = new ArrayList<ArmyRegimentWrapper>();

	public String getClimate() {
		return this.climate;
	}

	public void setClimate(String climate) {
		this.climate = climate;
	}

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

	public int getMorale() {
		return this.morale;
	}

	public void setMorale(int morale) {
		this.morale = morale;
	}

	public int getTransports() {
		return this.transports;
	}

	public void setTransports(int transports) {
		this.transports = transports;
	}

	public int getWarmachines() {
		return this.warmachines;
	}

	public void setWarmachines(int warmachines) {
		this.warmachines = warmachines;
	}

	public int getWarships() {
		return this.warships;
	}

	public void setWarships(int warships) {
		this.warships = warships;
	}

	public ArrayList<ArmyRegimentWrapper> getRegiments() {
		return this.regiments;
	}

	public void setRegiments(ArrayList<ArmyRegimentWrapper> regiments) {
		this.regiments = regiments;
	}

	public void addRegiment(ArmyRegimentWrapper arw) {
		this.regiments.add(arw);
	}

	public void updateArmy(Army army) {
		army.setFood(getFood());
		try {
			army.setMorale(getMorale());
		} catch (Exception exc) {
		}
		;
		army.setNavy(getTransports() + getWarships() > 0);
		army.setElement(ArmyElementType.Warships, getWarships());
		army.setElement(ArmyElementType.Transports, getTransports());
		army.setElement(ArmyElementType.WarMachimes, getWarmachines());
		for (ArmyRegimentWrapper arw : getRegiments()) {
			ArmyElementType t = getArmyElementType(arw.getTroopType());
			boolean found = false;
			for (ArmyElement ae : army.getElements()) {
				if (ae.getArmyElementType().equals(t)) {
					ae.setTraining(arw.getTraining());
					ae.setWeapons(arw.getWeapons());
					ae.setArmor(arw.getArmor());
					found = true;
				}
			}
			if (!found) {
				ArmyElement ae = new ArmyElement(t, arw.getNumber());
				ae.setTraining(arw.getTraining());
				ae.setWeapons(arw.getWeapons());
				ae.setArmor(arw.getArmor());
				// army.getElements().add(ae);
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
}
