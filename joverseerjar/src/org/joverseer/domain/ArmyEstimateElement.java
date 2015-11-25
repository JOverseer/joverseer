package org.joverseer.domain;

import java.io.Serializable;

/**
 * Stores information about an element of an army estimate.
 * 
 * @author Marios Skounakis
 *
 */
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
		return this.armor;
	}
	public void setArmor(int armor) {
		this.armor = armor;
	}
	public String getArmorDescription() {
		return this.armorDescription;
	}
	public void setArmorDescription(String armorDescription) {
		this.armorDescription = armorDescription;
	}
	public String getArmorRange() {
		return this.armorRange;
	}
	public void setArmorRange(String armorRange) {
		this.armorRange = armorRange;
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
	public String getTrainingDescription() {
		return this.trainingDescription;
	}
	public void setTrainingDescription(String trainingDescription) {
		this.trainingDescription = trainingDescription;
	}
	public String getTrainingRange() {
		return this.trainingRange;
	}
	public void setTrainingRange(String trainingRange) {
		this.trainingRange = trainingRange;
	}
	public ArmyElementType getType() {
		return this.type;
	}
	public void setType(ArmyElementType type) {
		this.type = type;
	}
	public int getWeapons() {
		return this.weapons;
	}
	public void setWeapons(int weapons) {
		this.weapons = weapons;
	}
	public String getWeaponsDescription() {
		return this.weaponsDescription;
	}
	public void setWeaponsDescription(String weaponsDescription) {
		this.weaponsDescription = weaponsDescription;
	}
	public String getWeaponsRange() {
		return this.weaponsRange;
	}
	public void setWeaponsRange(String weaponsRange) {
		this.weaponsRange = weaponsRange;
	}
	
	

}
