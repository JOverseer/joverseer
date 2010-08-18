package org.joverseer.support.readers.newXml;

import org.joverseer.domain.Character;
import org.joverseer.domain.InformationSourceEnum;
import org.joverseer.game.Game;
import org.joverseer.game.Turn;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.support.NationMap;
import org.joverseer.support.infoSources.InfoSource;

public class HostageWrapper {
	String nameId;
	String nation;
	String heldBy;
	int hexNo;
	public String getNameId() {
		return nameId;
	}
	public void setNameId(String nameId) {
		this.nameId = nameId;
	}
	public String getNation() {
		return nation;
	}
	public void setNation(String nation) {
		this.nation = nation;
	}
	public String getHeldBy() {
		return heldBy;
	}
	public void setHeldBy(String heldBy) {
		this.heldBy = heldBy;
	}
	public int getHexNo() {
		return hexNo;
	}
	
	public void setHexNo(int hexNo) {
		this.hexNo = hexNo;
	}
	
	public void updateGame(Game game, Turn t, InfoSource infoSource) {
        Character c;
        c = t.getCharByName(getNameId());
        if (c == null) {
	        c = new Character();
	        c.setName(getNameId());
	        c.setId(Character.getIdFromName(getNameId()));
	        c.setHexNo(getHexNo());
	        c.setNation(NationMap.getNationFromName(getNation()));
	        c.setInformationSource(InformationSourceEnum.limited);
	        c.setInfoSource(infoSource);
	        t.getContainer(TurnElementsEnum.Character).addItem(c);
        }
        c.setHostage(true);
        if (c.getNationNo() == 0) {
        	c.setNation(NationMap.getNationFromName(getNation()));
        }
        Character holder = t.getCharByName(getHeldBy());
        if (holder != null) holder.addHostage(c.getName());
    }
}
