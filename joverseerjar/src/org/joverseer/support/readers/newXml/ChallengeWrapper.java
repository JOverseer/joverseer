package org.joverseer.support.readers.newXml;

import java.util.ArrayList;

import org.joverseer.support.StringUtils;

public class ChallengeWrapper {
	ArrayList<String> lines = new ArrayList<String>();
	int hexNo;
	String character;
	
	public void addLine(String line) {
		this.lines.add(line);
	}

	public int getHexNo() {
		return this.hexNo;
	}

	public void setHexNo(int hexNo) {
		this.hexNo = hexNo;
	}

	public String getCharacter() {
		return this.character;
	}

	public void setCharacter(String character) {
		this.character = character;
	}
	
	public String getDescription() {
		String ret = "";
		for (String l : this.lines) {
			if (!l.endsWith(".")) l = l + ".";
			ret += (ret.equals("") ? "" : "\n") + l;
		}
		return ret;
	}
	
	public void parse() {
		if (this.lines.size() > 1) {
			String header = this.lines.get(0);
			setCharacter(StringUtils.getUniquePart(header, "Challenge from ", " at \\d\\d\\d\\d(\\.)?$", false, false));
			String hex = StringUtils.getUniquePart(header, "Challenge from " + getCharacter() + " at ", "(\\.)?$", false, false);
			try {
				setHexNo(Integer.parseInt(hex));
			}
			catch (NumberFormatException e) {
				
			}
		}
	}
	
	
}
