package org.joverseer.support.readers.newXml;

import java.util.ArrayList;

import org.joverseer.domain.Challenge;
import org.joverseer.game.Game;
import org.joverseer.game.Turn;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.support.StringUtils;

public class ChallengeWrapper {
	ArrayList<String> lines = new ArrayList<String>();
	int hexNo;
	String character;
	
	public void addLine(String line) {
		lines.add(line);
	}

	public int getHexNo() {
		return hexNo;
	}

	public void setHexNo(int hexNo) {
		this.hexNo = hexNo;
	}

	public String getCharacter() {
		return character;
	}

	public void setCharacter(String character) {
		this.character = character;
	}
	
	public String getDescription() {
		String ret = "";
		for (String l : lines) {
			if (!l.endsWith(".")) l = l + ".";
			ret += (ret.equals("") ? "" : "\n") + l;
		}
		return ret;
	}
	
	public void parse() {
		if (lines.size() > 1) {
			String header = lines.get(0);
			setCharacter(StringUtils.getUniquePart(header, "Challenge from ", " at \\d\\d\\d\\d(\\.)?$", false, false));
			String hex = StringUtils.getUniquePart(header, "Challenge from " + getCharacter() + " at ", "(\\.)?$", false, false);
			try {
				setHexNo(Integer.parseInt(hex));
			}
			catch (Exception e) {
				
			}
		}
	}
	
	
}
