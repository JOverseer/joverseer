package org.joverseer.support.infoSources.spells;


public class DerivedFromRevealCharacterInfoSource extends DerivedFromSpellInfoSource {
    public DerivedFromRevealCharacterInfoSource(int turnNo, int nationNo, String casterName, int hexNo) {
        setTurnNo(turnNo);
        setNationNo(nationNo);
        setCasterName(casterName);
        setHexNo(hexNo);
    }
    
    public String getSpell() {
        return "RC";
    }
}
