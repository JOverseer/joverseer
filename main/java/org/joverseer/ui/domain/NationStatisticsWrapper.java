package org.joverseer.ui.domain;

import org.joverseer.domain.IBelongsToNation;

public class NationStatisticsWrapper implements IBelongsToNation {
	Integer nationNo;
	int characters;
	int charactersInCapital;
	int commanders;
	int popCenters;
	int taxBase;
	int cities;
	int majorTowns;
	int towns;
	int villages;
	int camps;
	int armies;
	int navies;
	int warships;
	int transports;
	int armyEHI;
	int troopCount;
	
	public int getArmies() {
		return armies;
	}
	public void setArmies(int armies) {
		this.armies = armies;
	}
	public int getArmyEHI() {
		return armyEHI;
	}
	public void setArmyEHI(int armyEHI) {
		this.armyEHI = armyEHI;
	}
	public int getCharacters() {
		return characters;
	}
	public void setCharacters(int characters) {
		this.characters = characters;
	}
	public int getCharactersInCapital() {
		return charactersInCapital;
	}
	public void setCharactersInCapital(int charactersInCapital) {
		this.charactersInCapital = charactersInCapital;
	}
	public int getCommanders() {
		return commanders;
	}
	public void setCommanders(int commanders) {
		this.commanders = commanders;
	}
	public Integer getNationNo() {
		return nationNo;
	}
	public void setNationNo(Integer nationNo) {
		this.nationNo = nationNo;
	}
	public int getPopCenters() {
		return popCenters;
	}
	public void setPopCenters(int popCenters) {
		this.popCenters = popCenters;
	}
	public int getTaxBase() {
		return taxBase;
	}
	public void setTaxBase(int taxBase) {
		this.taxBase = taxBase;
	}
	public int getTroopCount() {
		return troopCount;
	}
	public void setTroopCount(int troopCount) {
		this.troopCount = troopCount;
	}
	public int getCities() {
		return cities;
	}
	public void setCities(int cities) {
		this.cities = cities;
	}
	public int getMajorTowns() {
		return majorTowns;
	}
	public void setMajorTowns(int majorTowns) {
		this.majorTowns = majorTowns;
	}
	public int getCamps() {
		return camps;
	}
	public void setCamps(int camps) {
		this.camps = camps;
	}
	public int getTowns() {
		return towns;
	}
	public void setTowns(int towns) {
		this.towns = towns;
	}
	public int getVillages() {
		return villages;
	}
	public void setVillages(int villages) {
		this.villages = villages;
	}
	public int getNavies() {
		return navies;
	}
	public void setNavies(int navies) {
		this.navies = navies;
	}
	public int getTransports() {
		return transports;
	}
	public void setTransports(int transports) {
		this.transports = transports;
	}
	public int getWarships() {
		return warships;
	}
	public void setWarships(int warships) {
		this.warships = warships;
	}
	
	
}
