package org.joverseer.support.readers.newXml;

import java.util.ArrayList;
import org.joverseer.domain.Character;

public class CharacterMessageWrapper {
	String charId;
	ArrayList lines = new ArrayList();
	
	public String getCharId() {
		return charId;
	}
	public void setCharId(String charId) {
		this.charId = charId;
	}
	public ArrayList getLines() {
		return lines;
	}
	public void setLines(ArrayList lines) {
		this.lines = lines;
	}
	
	public void addLine(String line) {
		lines.add(line);
	}
	
	public String getOrdersAsString() {
		String ret = "";
		for (String line : (ArrayList<String>)lines) {
			ret += (ret.equals("") ? "" : " ") + line;
		}
		return ret;
	}
	
	public void updateCharacter(Character c) {
		c.setOrderResults(getOrdersAsString());
	}
}
