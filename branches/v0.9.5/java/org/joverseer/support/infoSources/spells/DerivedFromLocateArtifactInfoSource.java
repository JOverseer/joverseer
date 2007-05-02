package org.joverseer.support.infoSources.spells;



public class DerivedFromLocateArtifactInfoSource extends DerivedFromSpellInfoSource {
    public DerivedFromLocateArtifactInfoSource(int turnNo, int nationNo, String casterName, int hexNo) {
        setTurnNo(turnNo);
        setNationNo(nationNo);
        setCasterName(casterName);
        setHexNo(hexNo);
    }
    
    public String getSpell() {
        return "LA";
    }
}
