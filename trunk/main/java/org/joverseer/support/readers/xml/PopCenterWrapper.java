package org.joverseer.support.readers.xml;

import org.joverseer.domain.FortificationSizeEnum;
import org.joverseer.domain.HarborSizeEnum;
import org.joverseer.domain.InformationSourceEnum;
import org.joverseer.domain.PopulationCenter;
import org.joverseer.domain.PopulationCenterSizeEnum;

/**
 * Holds information about pop centers (from xml turns)
 * 
 * @author Marios Skounakis
 */
public class PopCenterWrapper {

	int hexID;
	String name;
	int nation;
	int nationAllegience;
	int fortificationLevel;
	int size;
	int dock;
	int capital;
	int loyalty;
	int informationSource;
	int hidden;

	public int getCapital() {
		return capital;
	}

	public void setCapital(int capital) {
		this.capital = capital;
	}

	public int getDock() {
		return dock;
	}

	public void setDock(int dock) {
		this.dock = dock;
	}

	public int getFortificationLevel() {
		return fortificationLevel;
	}

	public void setFortificationLevel(int fortificationLevel) {
		this.fortificationLevel = fortificationLevel;
	}

	public int getHidden() {
		return hidden;
	}

	public void setHidden(int hidden) {
		this.hidden = hidden;
	}

	public int getInformationSource() {
		return informationSource;
	}

	public void setInformationSource(int informationSource) {
		this.informationSource = informationSource;
	}

	public int getLoyalty() {
		return loyalty;
	}

	public void setLoyalty(int loyalty) {
		this.loyalty = loyalty;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getNation() {
		return nation;
	}

	public void setNation(int nation) {
		this.nation = nation;
	}

	public int getNationAllegience() {
		return nationAllegience;
	}

	public void setNationAllegience(int nationAllegience) {
		this.nationAllegience = nationAllegience;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public int getHexID() {
		return hexID;
	}

	public void setHexID(int hexID) {
		this.hexID = hexID;
	}

	public PopulationCenter getPopulationCenter() {
		PopulationCenter pc = new PopulationCenter();
		pc.setX(getHexID() / 100);
		pc.setY(getHexID() % 100);
		pc.setName(getName());
		pc.setLoyalty(getLoyalty());
		pc.setNationNo(getNation());
		pc.setHidden(getHidden() == 1);
		pc.setCapital(getCapital() == 1);
		switch (getFortificationLevel()) {
		case 0:
			pc.setFortification(FortificationSizeEnum.none);
			break;
		case 1:
			pc.setFortification(FortificationSizeEnum.tower);
			break;
		case 2:
			pc.setFortification(FortificationSizeEnum.fort);
			break;
		case 3:
			pc.setFortification(FortificationSizeEnum.castle);
			break;
		case 4:
			pc.setFortification(FortificationSizeEnum.keep);
			break;
		case 5:
			pc.setFortification(FortificationSizeEnum.citadel);
			break;
		default:
			throw new RuntimeException("Illegal fortification level " + getFortificationLevel());

		}
		switch (getSize()) {
		case 6:
			pc.setSize(PopulationCenterSizeEnum.ruins);
			break;
		case 1:
			pc.setSize(PopulationCenterSizeEnum.camp);
			break;
		case 2:
			pc.setSize(PopulationCenterSizeEnum.village);
			break;
		case 3:
			pc.setSize(PopulationCenterSizeEnum.town);
			break;
		case 4:
			pc.setSize(PopulationCenterSizeEnum.majorTown);
			break;
		case 5:
			pc.setSize(PopulationCenterSizeEnum.city);
			break;
		case 0:
			// TODO expecting clarification, this only exists in new xmls
			pc.setSize(PopulationCenterSizeEnum.ruins);
			break;
		default:
			throw new RuntimeException("Illegal size " + getSize());
		}
		switch (getDock()) {
		case 0:
			pc.setHarbor(HarborSizeEnum.none);
			break;
		case 1:
			pc.setHarbor(HarborSizeEnum.harbor);
			break;
		case 2:
			pc.setHarbor(HarborSizeEnum.port);
			break;
		}
		// todo set allegiance
		switch (getInformationSource()) {
		case 0:
			pc.setInformationSource(InformationSourceEnum.exhaustive);
			break;
		case 1:
			pc.setInformationSource(InformationSourceEnum.exhaustive);
			break;
		case 2:
			pc.setInformationSource(InformationSourceEnum.detailed);
			break;
		case 3:
			pc.setInformationSource(InformationSourceEnum.some);
			break;
		case 4:
			pc.setInformationSource(InformationSourceEnum.limited);
			break;
		}

		return pc;
	}

}
