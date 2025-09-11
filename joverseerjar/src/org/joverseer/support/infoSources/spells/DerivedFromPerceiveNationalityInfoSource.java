package org.joverseer.support.infoSources.spells;

public class DerivedFromPerceiveNationalityInfoSource extends DerivedFromSpellInfoSource {

	private static final long serialVersionUID = -2647637336024999006L;
	
	public DerivedFromPerceiveNationalityInfoSource(int turnNo, int nationNo, String casterName){
        setTurnNo(turnNo);
        setNationNo(nationNo);
        setCasterName(casterName);
    }
    
    @Override
	public String getSpell() {
        return "PN";
    }

}
