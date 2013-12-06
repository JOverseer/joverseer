package org.joverseer.ui.domain;

import java.util.ArrayList;

import org.joverseer.domain.Army;
import org.joverseer.domain.IBelongsToNation;
import org.joverseer.game.Game;
import org.joverseer.game.Turn;
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
	int hostages;
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
		return this.allegiance;
	}

	public void setAllegiance(NationAllegianceEnum allegiance) {
		this.allegiance = allegiance;
	}

	public int getArmies() {
		return this.armies;
	}

	public void setArmies(int armies) {
		this.armies = armies;
	}

	public int getArmyEHI() {
		if (this.armyEHI == null) {
			this.armyEHI = 0;
			Game g = ((GameHolder) Application.instance().getApplicationContext().getBean("gameHolder")).getGame();
			if (!Game.isInitialized(g))
				return 0;
			Turn t = g.getTurn();
			ArrayList<Army> armies1;
			if (this.allegiance == null) {
				armies1 = t.getArmies().findAllByProperty("nationNo", getNationNo());
			} else {
				armies1 = t.getArmies().findAllByProperty("nationAllegiance", getAllegiance());
			}
			for (Army a : armies1) {
				this.armyEHI = a.getENHI() + this.armyEHI;
			}
		}
		return this.armyEHI;
	}

	public void setArmyEHI(int armyEHI) {
		this.armyEHI = armyEHI;
	}

	public int getCharacters() {
		return this.characters;
	}

	public void setCharacters(int characters) {
		this.characters = characters;
	}

	public int getHostages() {
		return this.hostages;
	}

	public void setHostages(int hostages) {
		this.hostages = hostages;
	}

	public int getCharactersInCapital() {
		return this.charactersInCapital;
	}

	public void setCharactersInCapital(int charactersInCapital) {
		this.charactersInCapital = charactersInCapital;
	}

	public int getCommanders() {
		return this.commanders;
	}

	public void setCommanders(int commanders) {
		this.commanders = commanders;
	}

	@Override
	public Integer getNationNo() {
		return this.nationNo;
	}

	@Override
	public void setNationNo(Integer nationNo) {
		this.nationNo = nationNo;
	}

	public int getPopCenters() {
		return this.popCenters;
	}

	public void setPopCenters(int popCenters) {
		this.popCenters = popCenters;
	}

	public int getTaxBase() {
		return this.taxBase;
	}

	public void setTaxBase(int taxBase) {
		this.taxBase = taxBase;
	}

	public int getTroopCount() {
		return this.troopCount;
	}

	public void setTroopCount(int troopCount) {
		this.troopCount = troopCount;
	}

	public int getCities() {
		return this.cities;
	}

	public void setCities(int cities) {
		this.cities = cities;
	}

	public int getMajorTowns() {
		return this.majorTowns;
	}

	public void setMajorTowns(int majorTowns) {
		this.majorTowns = majorTowns;
	}

	public int getCamps() {
		return this.camps;
	}

	public void setCamps(int camps) {
		this.camps = camps;
	}

	public int getTowns() {
		return this.towns;
	}

	public void setTowns(int towns) {
		this.towns = towns;
	}

	public int getVillages() {
		return this.villages;
	}

	public void setVillages(int villages) {
		this.villages = villages;
	}

	public int getNavies() {
		return this.navies;
	}

	public void setNavies(int navies) {
		this.navies = navies;
	}

	public int getTransports() {
		return this.transports;
	}

	public void setTransports(int transports) {
		this.transports = transports;
	}

	public int getWarships() {
		return this.warships;
	}

	public void setWarships(int warships) {
		this.warships = warships;
	}

	public void add(NationStatisticsWrapper nsw) {
		this.armies += nsw.getArmies();
		this.cities += nsw.getCities();
		this.majorTowns += nsw.getMajorTowns();
		this.towns += nsw.getTowns();
		this.villages += nsw.getVillages();
		this.camps += nsw.getCamps();
		this.taxBase += nsw.getTaxBase();
		this.characters += nsw.getCharacters();
		this.hostages += nsw.getHostages();
		this.navies += nsw.getNavies();
		this.transports += nsw.getTransports();
		this.warships += nsw.getWarships();
		this.troopCount += nsw.getTroopCount();
		this.popCenters += nsw.getPopCenters();

	}
}
