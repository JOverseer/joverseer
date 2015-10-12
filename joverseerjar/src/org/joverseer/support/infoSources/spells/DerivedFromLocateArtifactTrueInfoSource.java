package org.joverseer.support.infoSources.spells;

/**
 * Information derived from a LAT spell.
 * Contains:
 * - the turn number
 * - the nation number of the caster
 * - the caster's name
 * - the hex at which the item was LAT'ed
 * 
 * @author Marios Skounakis
 *
 */
public class DerivedFromLocateArtifactTrueInfoSource extends DerivedFromSpellInfoSource {
    private static final long serialVersionUID = -3624828635554337540L;

	public DerivedFromLocateArtifactTrueInfoSource(int turnNo, int nationNo, String casterName, int hexNo) {
        setTurnNo(turnNo);
        setNationNo(nationNo);
        setCasterName(casterName);
        setHexNo(hexNo);
    }
    
    @Override
	public String getSpell() {
        return "LAT";
    }

}
