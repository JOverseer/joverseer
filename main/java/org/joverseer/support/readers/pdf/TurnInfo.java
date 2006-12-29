package org.joverseer.support.readers.pdf;

import org.joverseer.support.Container;

public class TurnInfo {
    int turnNo = -1;
    int nationNo = -1;
    String allegiance;
    Container nationRelations;
    Container populationCenters;
    Container characters;
    Container companies;
    Container combats;
    Container armies;
    Container encounters;

    public String getAllegiance() {
        return allegiance;
    }

    public void setAllegiance(String allegiance) {
        this.allegiance = allegiance;
    }

    public Container getNationRelations() {
        return nationRelations;
    }

    public void setNationRelations(Container nationRelations) {
        this.nationRelations = nationRelations;
    }

    public Container getPopulationCenters() {
        return populationCenters;
    }

    public void setPopulationCenters(Container populationCenters) {
        this.populationCenters = populationCenters;
    }

    public Container getCharacters() {
        return characters;
    }

    public void setCharacters(Container characters) {
        this.characters = characters;
    }


    public Container getCompanies() {
        return companies;
    }


    public void setCompanies(Container companies) {
        this.companies = companies;
    }

    
    public int getTurnNo() {
        return turnNo;
    }

    
    public void setTurnNo(int turnNo) {
        this.turnNo = turnNo;
    }

    
    public int getNationNo() {
        return nationNo;
    }

    
    public void setNationNo(int nationNo) {
        this.nationNo = nationNo;
    }

    
    public Container getCombats() {
        return combats;
    }

    
    public void setCombats(Container combats) {
        this.combats = combats;
    }

    
    public Container getArmies() {
        return armies;
    }

    
    public void setArmies(Container armies) {
        this.armies = armies;
    }

    
    public Container getEncounters() {
        return encounters;
    }

    
    public void setEncounters(Container encounters) {
        this.encounters = encounters;
    }

    
    
}
