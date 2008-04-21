package org.joverseer.support.readers.newXml;

public class ArmyRegimentWrapper {
	String troopType;
	int number;
	String description;
	int training;
	int weapons;
	int armor;
	public int getArmor() {
		return armor;
	}
	public void setArmor(int armor) {
		this.armor = armor;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public int getNumber() {
		return number;
	}
	public void setNumber(int number) {
		this.number = number;
	}
	public int getTraining() {
		return training;
	}
	public void setTraining(int training) {
		this.training = training;
	}
	public String getTroopType() {
		return troopType;
	}
	public void setTroopType(String troopType) {
		this.troopType = troopType;
	}
	public int getWeapons() {
		return weapons;
	}
	public void setWeapons(int weapons) {
		this.weapons = weapons;
	}
	
	
}
