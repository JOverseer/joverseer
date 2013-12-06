package org.joverseer.support.readers.newXml;

import org.joverseer.domain.Character;
import org.joverseer.domain.InformationSourceEnum;
import org.joverseer.metadata.domain.Nation;
import org.joverseer.support.GameHolder;

public class DoubleAgentWrapper {
	int hexNo;
	String name;
	String nation;
	String report;

	
	public String getNation() {
		return this.nation;
	}

	public void setNation(String nation) {
		this.nation = nation;
	}

	public int getHexNo() {
		return this.hexNo;
	}

	public void setHexNo(int hexNo) {
		this.hexNo = hexNo;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getReport() {
		return this.report;
	}

	public void setReport(String report) {
		this.report = report;
	}
	
	public Character getCharacter() {
        Character c = new Character();
        c.setName(getName());
        c.setId(Character.getIdFromName(getName()));
        c.setHexNo(getHexNo());
        Nation n = GameHolder.instance().getGame().getMetadata().getNationByName(getNation());
        c.setNationNo(n == null ? 0 : n.getNumber());
        c.setInformationSource(InformationSourceEnum.limited);
        c.setOrderResults(getReport());
        return c;
    }
}
