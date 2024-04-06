package org.joverseer.tools.turnReport;

import org.joverseer.domain.Character;
import org.joverseer.domain.Encounter;
import org.joverseer.domain.IBelongsToNation;
import org.joverseer.game.Turn;
import org.joverseer.tools.infoCollectors.characters.AdvancedCharacterWrapper;

public class CharacterReport extends BaseReportObject implements IBelongsToNation {
	Character character;
	AdvancedCharacterWrapper characterWrapper;

	//dependencies
	Turn turn;
	public CharacterReport(Turn turn) {
		super();
		this.turn = turn;
	}

	public CharacterReport(Character c,Turn turn) {
		this(turn);
		setName(c.getName());
		setNationNo(c.getNationNo());
		setHexNo(c.getHexNo());
		setCharacter(c);
	}
	public CharacterReport(Character c,ObjectModificationType modification,Turn turn) {
		this(c,turn);
		this.setModification(modification);
	}
	public CharacterReport(Character c,ObjectModificationType modification,String notes,Turn turn) {
		this(c,modification,turn);
		this.setNotes(notes);
	}

	public CharacterReport(AdvancedCharacterWrapper c,Turn turn) {
		this(turn);
		setName(c.getName());
		setNationNo(c.getNationNo());
		setHexNo(c.getHexNo());
		setCharacterWrapper(c);
	}

	public AdvancedCharacterWrapper getCharacterWrapper() {
		return this.characterWrapper;
	}

	public void setCharacterWrapper(AdvancedCharacterWrapper characterWrapper) {
		this.characterWrapper = characterWrapper;
	}

	public Character getCharacter() {
		return this.character;
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
			if (s.equals(""))
				return "";
			return s + " (estimated)";
		}
		return "";
	}

	@Override
	public String getLinks() {
		String ret = super.getLinks() + (getCharacter() != null && getCharacter().getOrderResults() != null ? " <a href='http://event?report=" + getCharacter().getId().replace(" ", "_") + "'>Report</a>" : "");
		if(getCharacterWrapper() != null) {
			if(getCharacterWrapper().isDoubleAgent() == true && getCharacterWrapper().getOrderResults() != null) {
				ret = super.getLinks() + (" <a href='http://event?report=" + getCharacterWrapper().getId().replace(" ", "_") + "'>Report</a>");
			}
		}
		for (Encounter enc : this.turn.getEncounters(getName())) {
			ret += " <a href='http://event?enc=" + enc.getHexNo() + "," + Character.getIdFromName(enc.getCharacter()).replace(" ", "_") + "'>Enc</a>";
		}

		return ret;
	}

}
