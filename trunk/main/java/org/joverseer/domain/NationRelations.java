package org.joverseer.domain;

import org.joverseer.metadata.domain.NationAllegianceEnum;

import java.util.HashMap;
import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: mskounak
 * Date: 6 Δεκ 2006
 * Time: 9:28:54 μμ
 * To change this template use File | Settings | File Templates.
 */
public class NationRelations implements IBelongsToNation, Serializable {
    int nationNo;
    NationRelationsEnum[] relations = new NationRelationsEnum[26];
    NationAllegianceEnum allegiance;

    public int getNationNo() {
        return nationNo;
    }

    public void setNationNo(int nationNo) {
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
        for (int i=0; i<26; i++) {
            c.setRelationsFor(i, getRelationsFor(i));
        }
        return c;
    }
}
