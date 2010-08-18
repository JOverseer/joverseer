package org.joverseer.ui.domain;

import java.util.ArrayList;

import org.joverseer.domain.Army;
import org.joverseer.domain.IBelongsToNation;
import org.joverseer.game.Game;
import org.joverseer.game.Turn;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.metadata.domain.NationAllegianceEnum;
import org.joverseer.support.GameHolder;
import org.springframework.richclient.application.Application;

/**
 * Wraps data for the nation statistics list view
 * 
 * @author Marios Skounakis
 */
public class NationStatisticsWrapper implements IBelongsToNation {
	NationAllegianceEnum allegiance;
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
	Integer armyEHI = null;
	int troopCount;
	
	
	
	public NationAllegianceEnum getAllegiance() {
		return allegiance;
	}
	public void setAllegiance(NationAllegianceEnum allegiance) {
		this.allegiance = allegiance;
	}
	public int getArmies() {
		return armies;
	}
	public void setArmies(int armies) {
		this.armies = armies;
	}
	public int getArmyEHI() {
		if (armyEHI == null) {
			armyEHI = 0;
			Game g = ((GameHolder) Application.instance().getApplicationContext().getBean("gameHolder")).getGame();
	        if (!Game.isInitialized(g)) return 0;
	        ArrayList items = new ArrayList();
	        Turn t = g.getTurn();
	        ArrayList<Army> armies;
	        if (allegiance == null) {
	        	armies = (ArrayList<Army>) t.getContainer(TurnElementsEnum.Army).findAllByProperty("nationNo",getNationNo());
	        } else {
	        	armies = (ArrayList<Army>) t.getContainer(TurnElementsEnum.Army).findAllByProperty("nationAllegiance",getAllegiance());
	        }
			for (Army a : armies) {
				armyEHI = a.getENHI() + armyEHI;
			}
		}
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
	
	public void add(NationStatisticsWrapper nsw) {
		armies += nsw.getArmies();
		cities += nsw.getCities();
		majorTowns += nsw.getMajorTowns();
		towns += nsw.getTowns();
		villages += nsw.getVillages();
		camps += nsw.getCamps();
		taxBase += nsw.getTaxBase();
		characters += nsw.getCharacters();
		navies += nsw.getNavies();
		transports += nsw.getTransports();
		warships += nsw.getWarships();
		troopCount += nsw.getTroopCount();
		
	}
}
