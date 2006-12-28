package org.joverseer.metadata.domain;

import org.joverseer.metadata.domain.NationAllegianceEnum;

import java.io.Serializable;


public class Nation implements Serializable {
    Integer number;
    String name;
    String shortName;
    NationAllegianceEnum allegiance;

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
}
