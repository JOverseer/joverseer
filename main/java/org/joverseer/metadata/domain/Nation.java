package org.joverseer.metadata.domain;

import org.joverseer.metadata.SNAEnum;
import org.joverseer.metadata.domain.NationAllegianceEnum;

import java.io.Serializable;
import java.util.ArrayList;


public class Nation implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 4715738430328876223L;
    Integer number;
    String name;
    String shortName;
    NationAllegianceEnum allegiance;
    ArrayList<SNAEnum> snas= new ArrayList<SNAEnum>();

    public Nation(int number, String name, String shortName) {
        this.name = name;
        this.number = number;
        this.shortName = shortName;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public NationAllegianceEnum getAllegiance() {
        return allegiance;
    }

    public void setAllegiance(NationAllegianceEnum allegiance) {
        this.allegiance = allegiance;
    }

    
    public ArrayList<SNAEnum> getSnas() {
        return snas;
    }

    
    public void setSnas(ArrayList<SNAEnum> snas) {
        this.snas = snas;
    }
    
    public boolean hasSna(SNAEnum sna) {
        return getSnas().contains(sna);
    }
}
