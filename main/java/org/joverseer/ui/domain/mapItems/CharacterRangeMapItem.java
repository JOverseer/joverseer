package org.joverseer.ui.domain.mapItems;

import org.joverseer.domain.Character;

/**
 * Handles the drawing of the character's movement range on the map It can also draw arbitrary ranges from a given hex
 * number (to support Long Stride and Fast Stride)
 * 
 * @author Marios Skounakis
 */
public class CharacterRangeMapItem extends AbstractMapItem {

    private static final long serialVersionUID = -1956879681002471184L;
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
        return this.hexNo;
    }

    public void setHexNo(int hexNo) {
        this.hexNo = hexNo;
    }

    public int getRange() {
        return this.range;
    }

    public void setRange(int range) {
        this.range = range;
    }

    @Override
	public String getDescription() {
        return String.format("{0} hexes range from hex {1}.", new Object[] {this.range, this.hexNo});
    }
}
