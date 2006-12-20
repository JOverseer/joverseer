package org.joverseer.support.infoSources.spells;


public class DerivedFromLocateArtifactTrueInfoSource extends DerivedFromSpellInfoSource {
    public DerivedFromLocateArtifactTrueInfoSource(int turnNo, int nationNo, String casterName, int hexNo) {
        setTurnNo(turnNo);
        setNationNo(nationNo);
        setCasterName(casterName);
        setHexNo(hexNo);
    }
    
    public String getSpell() {
        return "LAT";
    }

}
