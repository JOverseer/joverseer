package org.joverseer.ui.domain.mapItems;

import org.joverseer.domain.Character;

public class CharacterRangeMapItem extends AbstractMapItem {
	Character character;
	
	public CharacterRangeMapItem(Character character) {
		super();
		this.character = character;
	}
	
	public void setCharacter(Character character) {
		this.character = character;
	}

	public Character getCharacter() {
		return character;
	}

	public String getDescription() {
		return String.format("Range for character {0} located at {1}.", new Object[]{getCharacter().getName(), getCharacter().getHexNo()});
	}
}
