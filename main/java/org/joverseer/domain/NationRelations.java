package org.joverseer.domain;

import java.io.Serializable;

import org.joverseer.metadata.domain.NationAllegianceEnum;


public class NationRelations implements IBelongsToNation, Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = -3415693212063202826L;
    Integer nationNo;
    NationRelationsEnum[] relations = new NationRelationsEnum[26];
    NationAllegianceEnum allegiance;
    boolean eliminated = false;
    boolean removed = false;

    public Integer getNationNo() {
        return nationNo;
    }

    public void setNationNo(Integer nationNo) {
        this.nationNo = nationNo;
    }

    public NationAllegianceEnum getAllegiance() {
        return allegiance;
    }

    public void setAllegiance(NationAllegianceEnum allegiance) {
        this.allegiance = allegiance;
    }

    public NationRelationsEnum getRelationsFor(int nationNo) {
        return relations[nationNo];
    }

    public void setRelationsFor(int nationNo, NationRelationsEnum relation) {
        relations[nationNo] = relation;
    }
    
    public boolean getEliminated() {
        return eliminated;
    }
    
    public void setEliminated(boolean eliminated) {
        this.eliminated = eliminated;
    }
    
    

    public NationRelations clone() {
        NationRelations c = new NationRelations();
        c.setNationNo(getNationNo());
        c.setEliminated(getEliminated());
        c.setAllegiance(getAllegiance());
        for (int i = 0; i < 26; i++) {
            c.setRelationsFor(i, getRelationsFor(i));
        }
        return c;
    }
}
