package org.joverseer.tools.turnReport;

import org.joverseer.domain.IBelongsToNation;
import org.joverseer.domain.Character;
import org.joverseer.tools.infoCollectors.characters.AdvancedCharacterWrapper;

public class CharacterReport extends BaseReportObject implements IBelongsToNation {
	Character character;
	AdvancedCharacterWrapper characterWrapper;
	
	public CharacterReport() {
		super();
	}
	
	public CharacterReport(Character c) {
		super();
		setName(c.getName());
		setNationNo(c.getNationNo());
		setHexNo(c.getHexNo());
		setCharacter(c);
	}
	
	public CharacterReport(AdvancedCharacterWrapper c) {
		super();
		setName(c.getName());
		setNationNo(c.getNationNo());
		setHexNo(c.getHexNo());
		setCharacterWrapper(c);
	}
	

	public AdvancedCharacterWrapper getCharacterWrapper() {
		return characterWrapper;
	}

	public void setCharacterWrapper(AdvancedCharacterWrapper characterWrapper) {
		this.characterWrapper = characterWrapper;
	}

	public Character getCharacter() {
		return character;
	}

	public void setCharacter(Character character) {
		this.character = character;
	}

	@Override
	public String getExtraInfo() {
		if (getCharacter() != null) {
			return getCharacter().getStatString();
		}
		if (getCharacterWrapper() != null) {
			String s = getCharacterWrapper().getStatString();
			if (s.equals("")) return "";
			return  s + " (estimated)";
		}
		return "";
	}

	@Override
	public String getLinks() {
		return super.getLinks() +
		(getCharacter() != null && getCharacter().getOrderResults() != null ?
				" <a href='http://event?report=" + getCharacter().getId().replace(" ", "_") + "'>Report</a>"
				: "");
	}
	

}
