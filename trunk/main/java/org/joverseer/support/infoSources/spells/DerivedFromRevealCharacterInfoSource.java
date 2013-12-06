package org.joverseer.support.infoSources.spells;

/**
 * Information derived from a RC spell.
 * Contains:
 * - the turn number
 * - the nation number of the caster
 * - the caster's name
 * - the hex at which the item was RC'ed
 * 
 * @author Marios Skounakis
 *
 */
public class DerivedFromRevealCharacterInfoSource extends DerivedFromSpellInfoSource {
    private static final long serialVersionUID = 1946520381748059930L;

	public DerivedFromRevealCharacterInfoSource(int turnNo, int nationNo, String casterName, int hexNo) {
        setTurnNo(turnNo);
        setNationNo(nationNo);
        setCasterName(casterName);
        setHexNo(hexNo);
    }
    
    @Override
	public String getSpell() {
        return "RC";
    }
}
