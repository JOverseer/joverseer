package org.joverseer.ui.domain.mapItems;

import org.joverseer.domain.Character;

public class CharacterRangeMapItem extends AbstractMapItem {
	int hexNo;
	int range;
	
	public CharacterRangeMapItem(Character character) {
		super();
		this.hexNo = character.getHexNo();
		this.range = 12;
	}
	
	
	
	public CharacterRangeMapItem(int hexNo, int range) {
		super();
		this.hexNo = hexNo;
		this.range = range;
	}

	public int getHexNo() {
		return hexNo;
	}

	public void setHexNo(int hexNo) {
		this.hexNo = hexNo;
	}

	public int getRange() {
		return range;
	}

	public void setRange(int range) {
		this.range = range;
	}

	public String getDescription() {
		return String.format("{0} hexes range from hex {1}.", new Object[]{range, hexNo});
	}
}
