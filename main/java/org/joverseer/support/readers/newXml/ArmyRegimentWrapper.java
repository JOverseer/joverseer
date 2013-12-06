package org.joverseer.support.readers.newXml;

public class ArmyRegimentWrapper {
	String troopType;
	int number;
	String description;
	int training;
	int weapons;
	int armor;
	public int getArmor() {
		return this.armor;
	}
	public void setArmor(int armor) {
		this.armor = armor;
	}
	public String getDescription() {
		return this.description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public int getNumber() {
		return this.number;
	}
	public void setNumber(int number) {
		this.number = number;
	}
	public int getTraining() {
		return this.training;
	}
	public void setTraining(int training) {
		this.training = training;
	}
	public String getTroopType() {
		return this.troopType;
	}
	public void setTroopType(String troopType) {
		this.troopType = troopType;
	}
	public int getWeapons() {
		return this.weapons;
	}
	public void setWeapons(int weapons) {
		this.weapons = weapons;
	}
	
	
}
