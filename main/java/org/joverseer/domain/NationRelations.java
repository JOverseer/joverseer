package org.joverseer.domain;

import java.io.Serializable;

import org.joverseer.metadata.domain.NationAllegianceEnum;

/**
 * Stores the relation status for a given nation. This includes
 * - the allegiance
 * - the relations to the rest of the nations
 * - whether it has been eliminated or removed from the game
 * 
 * @author Marios Skounakis
 *
 */

public class NationRelations implements IBelongsToNation, Serializable {
    private static final long serialVersionUID = -3415693212063202826L;
    Integer nationNo;
    NationRelationsEnum[] relations = new NationRelationsEnum[26];
    NationAllegianceEnum allegiance;
    boolean eliminated = false;
    boolean removed = false;

    @Override
	public Integer getNationNo() {
        return this.nationNo;
    }

    @Override
	public void setNationNo(Integer nationNo) {
        this.nationNo = nationNo;
    }

    public NationAllegianceEnum getAllegiance() {
        return this.allegiance;
    }

    public void setAllegiance(NationAllegianceEnum allegiance) {
        this.allegiance = allegiance;
    }

    public NationRelationsEnum getRelationsFor(int nationNo1) {
        return this.relations[nationNo1];
    }

    public void setRelationsFor(int nationNo, NationRelationsEnum relation) {
        this.relations[nationNo] = relation;
    }
    
    public boolean getEliminated() {
        return this.eliminated;
    }
    
    public void setEliminated(boolean eliminated) {
        this.eliminated = eliminated;
    }
    
    

    @Override
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
