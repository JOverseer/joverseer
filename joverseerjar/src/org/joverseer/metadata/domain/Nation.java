package org.joverseer.metadata.domain;

import org.joverseer.metadata.SNAEnum;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Background information about a Nation. It holds:
 * - nation number
 * - nation name
 * - nation short name
 * - starting nation allegiance
 * - an arraylist of the nation SNAs
 * - a flag whether the nation has been eliminated
 * - a flag whether the nation has been removed from the game (e.g. for grudge games)
 *
 * @author Marios Skounakis
 *
 */
public class Nation implements Serializable {
    private static final long serialVersionUID = 4715738430328876223L;
    Integer number;
    String name;
    String shortName;
    NationAllegianceEnum allegiance;
    ArrayList<SNAEnum> snas= new ArrayList<SNAEnum>();
    boolean removed = false;
    boolean eliminated = false;
    int NPCsRecruited = 0;

    public Nation(int number, String name, String shortName) {
        this.name = name;
        this.number = Integer.valueOf(number);
        this.shortName = shortName;
    }

    public String getShortName() {
        return this.shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getNumber() {
        return this.number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public NationAllegianceEnum getAllegiance() {
        return this.allegiance;
    }

    public void setAllegiance(NationAllegianceEnum allegiance) {
        this.allegiance = allegiance;
    }


    public ArrayList<SNAEnum> getSnas() {
        return this.snas;
    }


    public void setSnas(ArrayList<SNAEnum> snas) {
        this.snas = snas;
    }

    public boolean hasSna(SNAEnum sna) {
        return getSnas().contains(sna);
    }


    public boolean getRemoved() {
        return this.removed;
    }


    public void setRemoved(boolean removed) {
        this.removed = removed;
    }


    public boolean getEliminated() {
        return this.eliminated;
    }


    public void setEliminated(boolean eliminated) {
        this.eliminated = eliminated;
    }
    public int getNPCsRecruited() {
    	return this.NPCsRecruited;
    }
    public void setNPCsRecruited(int NPCsRecruited) {
    	this.NPCsRecruited = NPCsRecruited;
    }

	@Override
	public String toString() {
		return this.getName();
	}

	public boolean isActivePlayer() {
		return (!this.eliminated) && (!this.removed) && (this.number >0) && (this.number < 26);
	}
}
