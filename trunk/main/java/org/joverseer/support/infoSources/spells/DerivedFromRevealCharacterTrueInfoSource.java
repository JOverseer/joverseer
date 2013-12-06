package org.joverseer.support.infoSources.spells;

/**
 * Information derived from a RCT spell.
 * Contains:
 * - the turn number
 * - the nation number of the caster
 * - the caster's name
 * - the hex at which the item was RC'ed
 * 
 * @author Marios Skounakis
 *
 */

public class DerivedFromRevealCharacterTrueInfoSource extends DerivedFromSpellInfoSource {

    private static final long serialVersionUID = -4726437201909301356L;

    public DerivedFromRevealCharacterTrueInfoSource(int turnNo, int nationNo, String casterName, int hexNo) {
        setTurnNo(turnNo);
        setNationNo(nationNo);
        setCasterName(casterName);
        setHexNo(hexNo);
    }
    
    @Override
	public String getSpell() {
        return "RCT";
    }
}