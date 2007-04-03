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

    public NationRelations clone() {
        NationRelations c = new NationRelations();
        c.setNationNo(getNationNo());
        c.setAllegiance(getAllegiance());
        for (int i = 0; i < 26; i++) {
            c.setRelationsFor(i, getRelationsFor(i));
        }
        return c;
    }
}
