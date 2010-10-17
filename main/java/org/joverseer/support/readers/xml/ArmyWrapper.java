package org.joverseer.support.readers.xml;

import org.joverseer.domain.Army;
import org.joverseer.domain.ArmyElement;
import org.joverseer.domain.ArmyElementType;
import org.joverseer.domain.ArmySizeEnum;
import org.joverseer.domain.InformationSourceEnum;
import org.joverseer.metadata.GameMetadata;
import org.joverseer.metadata.domain.Nation;
import org.joverseer.metadata.domain.NationAllegianceEnum;
import org.joverseer.support.GameHolder;
import org.springframework.richclient.application.Application;

/**
 * Holds information about armies (from xml turns)
 * 
 * @author Marios Skounakis
 */
public class ArmyWrapper {
	int hexID;
	int nation;
	int nationAllegience;
	int size;
	int troopCount;
	String commander;
	String commanderTitle;
	String extraInfo;
	int navy;
	int informationSource;
	String charsTravellingWith;

	public String getCharsTravellingWith() {
		return charsTravellingWith;
	}

	public void setCharsTravellingWith(String charsTravellingWith) {
		this.charsTravellingWith = charsTravellingWith;
	}

	public String getCommander() {
		return commander;
	}

	public void setCommander(String commander) {
		this.commander = commander;
	}

	public String getCommanderTitle() {
		return commanderTitle;
	}

	public void setCommanderTitle(String commanderTitle) {
		this.commanderTitle = commanderTitle;
	}

	public String getExtraInfo() {
		return extraInfo;
	}

	public void setExtraInfo(String extraInfo) {
		this.extraInfo = extraInfo;
	}

	public int getHexID() {
		return hexID;
	}

	public void setHexID(int hexID) {
		this.hexID = hexID;
	}

	public int getInformationSource() {
		return informationSource;
	}

	public void setInformationSource(int informationSource) {
		this.informationSource = informationSource;
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

	public int getNavy() {
		return navy;
	}

	public void setNavy(int navy) {
		this.navy = navy;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public int getTroopCount() {
		return troopCount;
	}

	public void setTroopCount(int troopCount) {
		this.troopCount = troopCount;
	}

	public Army getArmy() {
		Army a = new Army();
		a.setX(getHexID() / 100);
		a.setY(getHexID() % 100);
		a.setCommanderName(getCommander());
		a.setCommanderTitle(getCommanderTitle());
		a.setTroopCount(getTroopCount());
		a.setNavy(getNavy() == 1);
		a.setNationNo(getNation());

		if (getExtraInfo() != null && !getExtraInfo().equals("")) {
			// parse
			String[] regiments = getExtraInfo().split(" ");
			for (int i = 0; i < regiments.length; i++) {
				String type = regiments[i].substring(regiments[i].length() - 2, regiments[i].length());
				String number = regiments[i].substring(0, regiments[i].length() - 2);
				int no = Integer.parseInt(number);
				if (type.equals("HC")) {
					a.getElements().add(new ArmyElement(ArmyElementType.HeavyCavalry, no));
				} else if (type.equals("LC")) {
					a.getElements().add(new ArmyElement(ArmyElementType.LightCavalry, no));
				} else if (type.equals("HI")) {
					a.getElements().add(new ArmyElement(ArmyElementType.HeavyInfantry, no));
				} else if (type.equals("LI")) {
					a.getElements().add(new ArmyElement(ArmyElementType.LightInfantry, no));
				} else if (type.equals("AR")) {
					a.getElements().add(new ArmyElement(ArmyElementType.Archers, no));
				} else if (type.equals("MA")) {
					a.getElements().add(new ArmyElement(ArmyElementType.MenAtArms, no));
				}
			}
		}

		switch (getSize()) {
		case 0:
			a.setSize(ArmySizeEnum.unknown);
			break;
		case 1:
			a.setSize(ArmySizeEnum.unknown);
			break;
		case 2:
			a.setSize(ArmySizeEnum.small);
			break;
		case 3:
			a.setSize(ArmySizeEnum.army);
			break;
		case 4:
			a.setSize(ArmySizeEnum.large);
			break;
		case 5:
			a.setSize(ArmySizeEnum.huge);
			break;
		default:
			throw new RuntimeException("Invalid army size " + getSize());
		}

		switch (getInformationSource()) {
		case 0:
			a.setInformationSource(InformationSourceEnum.exhaustive);
			break;
		case 1:
			a.setInformationSource(InformationSourceEnum.detailed);
			break;
		case 2:
			a.setInformationSource(InformationSourceEnum.someMore);
			break;
		case 3:
			if (getExtraInfo() != null && !getExtraInfo().equals("")) {
				a.setInformationSource(InformationSourceEnum.detailed);
			} else {
				a.setInformationSource(InformationSourceEnum.some);
			}
			break;
		case 4:
			a.setInformationSource(InformationSourceEnum.limited);
			break;
		default:
			throw new RuntimeException("Uknown information source " + getInformationSource());
		}

		GameMetadata gm = ((GameHolder) Application.instance().getApplicationContext().getBean("gameHolder")).getGame().getMetadata();

		switch (getNationAllegience()) {
		case 0:
			Nation nation = gm.getNationByNum(getNation());
			if (nation != null) {
				a.setNationAllegiance(nation.getAllegiance());
			}
			break;
		case 1:
			a.setNationAllegiance(NationAllegianceEnum.FreePeople);
			break;
		case 2:
			a.setNationAllegiance(NationAllegianceEnum.DarkServants);
			break;
		case 3:
			a.setNationAllegiance(NationAllegianceEnum.Neutral);
			break;
		}

		if (getCharsTravellingWith() != null && !getCharsTravellingWith().equals("")) {
			String[] chars = getCharsTravellingWith().split(" [,\\-] ");
			if (chars.length > 0) {
				for (String c : chars) {
					a.getCharacters().add(c);
				}
			}
		}
		// todo nation allegiance
		return a;
	}
}
