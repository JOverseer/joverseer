package org.joverseer.support.readers.newXml;

import org.joverseer.domain.Encounter;

public class EncounterWrapper {
	int reacting;
	String hex;
	String charId;
	String text;
	
	
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getCharId() {
		return charId;
	}
	public void setCharId(String charId) {
		this.charId = charId;
	}
	public String getHex() {
		return hex;
	}
	public void setHex(String hex) {
		this.hex = hex;
	}
	public int getReacting() {
		return reacting;
	}
	public void setReacting(int reacting) {
		this.reacting = reacting;
	}
	
	public Encounter getEncounter() {
        Encounter e = new Encounter();
        e.setCharacter(getCharId());
        try {
        	e.setHexNo(Integer.parseInt(getHex()));
        }
        catch (Exception exc) {
        	return null;
        }
        e.setDescription(getText());
        return e;
    }
}
