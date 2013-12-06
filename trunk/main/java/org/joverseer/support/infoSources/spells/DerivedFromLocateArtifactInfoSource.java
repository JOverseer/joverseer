package org.joverseer.support.infoSources.spells;


/**
 * Information derived from a LA spell.
 * Contains:
 * - the turn number
 * - the nation number of the caster
 * - the caster's name
 * - the hex at which the item was LA'ed
 * 
 * @author Marios Skounakis
 *
 */
public class DerivedFromLocateArtifactInfoSource extends DerivedFromSpellInfoSource {
	private static final long serialVersionUID = -2958513297779262354L;

	public DerivedFromLocateArtifactInfoSource(int turnNo, int nationNo, String casterName, int hexNo) {
        setTurnNo(turnNo);
        setNationNo(nationNo);
        setCasterName(casterName);
        setHexNo(hexNo);
    }
    
    @Override
	public String getSpell() {
        return "LA";
    }
}
