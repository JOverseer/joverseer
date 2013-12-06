package org.joverseer.support.infoSources.spells;

/**
 * Information derived from a DivCharsWithForces spell.
 * Contains:
 * - the turn number
 * - the nation number of the caster
 * - the caster's name
 * 
 * @author Marios Skounakis
 *
 */
public class DerivedFromDivCharWithForcesInfoSource extends DerivedFromSpellInfoSource {

    private static final long serialVersionUID = 2351076575012073071L;

    public DerivedFromDivCharWithForcesInfoSource(int turnNo, int nationNo, String casterName) {
        setTurnNo(turnNo);
        setNationNo(nationNo);
        setCasterName(casterName);
    }
    
    @Override
	public String getSpell() {
        return "DCWF";
    }
    

}
