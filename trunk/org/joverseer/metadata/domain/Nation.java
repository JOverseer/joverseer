package org.joverseer.metadata.domain;

/**
 * Created by IntelliJ IDEA.
 * User: mskounak
 * Date: Sep 10, 2006
 * Time: 11:29:47 PM
 * To change this template use File | Settings | File Templates.
 */
public class Nation {
    int number;
    String name;
    NationAllegianceEnum allegiance;

    public Nation(int number, String name, NationAllegianceEnum allegiance) {
        this.name = name;
        this.number = number;
        this.allegiance = allegiance;
    }

    public NationAllegianceEnum getAllegiance() {
        return allegiance;
    }

    public void setAllegiance(NationAllegianceEnum allegiance) {
        this.allegiance = allegiance;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }
}
