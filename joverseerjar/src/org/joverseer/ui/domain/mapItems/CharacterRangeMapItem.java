package org.joverseer.ui.domain.mapItems;

import org.joverseer.domain.Character;
import org.joverseer.ui.support.Messages;

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
        return Messages.getString("CharacterRangeMapItem.HexesFromHex", new Object[] {this.range, this.hexNo}); //$NON-NLS-1$
    }


	@Override
	public boolean isEquivalent(AbstractMapItem mi) {
		return (mi instanceof CharacterRangeMapItem)
				&& (this.range == ((CharacterRangeMapItem)mi).range) 
				&& (this.hexNo == ((CharacterRangeMapItem)mi).hexNo);
	}
}
