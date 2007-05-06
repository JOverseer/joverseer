package org.joverseer.domain;

import java.io.Serializable;

public class ArmyEstimateElement implements Serializable {

	private static final long serialVersionUID = 5958104833199695499L;

	String description;
	ArmyElementType type;
	int number;
	String weaponsDescription;
	String weaponsRange;
	int weapons;
	String armorDescription;
	String armorRange;
	int armor;
	String trainingDescription;
	String trainingRange;
	int training;

	public int getArmor() {
		return armor;
	}
	public void setArmor(int armor) {
		this.armor = armor;
	}
	public String getArmorDescription() {
		return armorDescription;
	}
	public void setArmorDescription(String armorDescription) {
		this.armorDescription = armorDescription;
	}
	public String getArmorRange() {
		return armorRange;
	}
	public void setArmorRange(String armorRange) {
		this.armorRange = armorRange;
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
	public String getTrainingDescription() {
		return trainingDescription;
	}
	public void setTrainingDescription(String trainingDescription) {
		this.trainingDescription = trainingDescription;
	}
	public String getTrainingRange() {
		return trainingRange;
	}
	public void setTrainingRange(String trainingRange) {
		this.trainingRange = trainingRange;
	}
	public ArmyElementType getType() {
		return type;
	}
	public void setType(ArmyElementType type) {
		this.type = type;
	}
	public int getWeapons() {
		return weapons;
	}
	public void setWeapons(int weapons) {
		this.weapons = weapons;
	}
	public String getWeaponsDescription() {
		return weaponsDescription;
	}
	public void setWeaponsDescription(String weaponsDescription) {
		this.weaponsDescription = weaponsDescription;
	}
	public String getWeaponsRange() {
		return weaponsRange;
	}
	public void setWeaponsRange(String weaponsRange) {
		this.weaponsRange = weaponsRange;
	}
	
	

}
