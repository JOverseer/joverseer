package org.joverseer.domain;

import java.io.Serializable;

import org.joverseer.support.StringUtils;
import org.joverseer.support.info.InfoUtils;

/**
 * Stores the narration for a given encounter as found in the pdf turn results.
 * 
 * @author Marios Skounakis
 * 
 */
public class Encounter implements IHasMapLocation, Serializable {
	private static final long serialVersionUID = -117862763508411685L;
	String character;
	int hexNo;
	String description;
	boolean canInvestigate = false;

	public boolean getCanInvestigate() {
		return this.canInvestigate;
	}

	public void setCanInvestigate(boolean canInvestigate) {
		this.canInvestigate = canInvestigate;
	}

	public String getCharacter() {
		return this.character;
	}

	public void setCharacter(String character) {
		this.character = character;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getHexNo() {
		return this.hexNo;
	}

	public void setHexNo(int hexNo) {
		this.hexNo = hexNo;
	}

	@Override
	public int getX() {
		return getHexNo() / 100;
	}

	@Override
	public int getY() {
		return getHexNo() % 100;
	}

	public String getCleanDescription() {
		return StringUtils.removeExtraspaces(StringUtils.removeAllNewline(getDescription()));
	}

	public boolean isReacting() {
		String cleanDescr = getCleanDescription();
		return cleanDescr.contains("How will " + getCharacter() + " react") || cleanDescr.contains("What word(s) or name will " + getCharacter() + " say");
	}

	public boolean isReaction() {
		return !isReacting() && !getCanInvestigate();
	}

	public boolean isDragon() {
		String cleanDescr = getCleanDescription();
		return cleanDescr.contains("Dragon");
	}

	public String getDragonName() {
		for (String p : StringUtils.getParts(getCleanDescription(), "\"", "\"", false, false)) {
			if (p == null)
				continue;
			p = p.replace(".", "").trim();
			if (InfoUtils.isDragon(p).booleanValue())
				return p;
		}
		return null;
	}
}
