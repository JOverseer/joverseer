package org.joverseer.support.readers.pdf;

import org.joverseer.support.Container;

/**
 * Container class for all the information read from a pdf turn.
 * 
 * @author Marios Skounakis
 */
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
    Container doubleAgents;
    Container challenges;
    Container hostages;
    Container snas;
    Container artifacts;
    Container anchoredShips;
    String date;
    String season;
    String nationName;

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

    
    public Container getDoubleAgents() {
        return doubleAgents;
    }

    
    public void setDoubleAgents(Container doubleAgents) {
        this.doubleAgents = doubleAgents;
    }

    
    public Container getChallenges() {
        return challenges;
    }

    
    public void setChallenges(Container challenges) {
        this.challenges = challenges;
    }

    
    public String getDate() {
        return date;
    }
    
    public void setDate(String date) {
        this.date = date;
    }
    
    public String getSeason() {
        return season;
    }
    
    public void setSeason(String season) {
        this.season = season;
    }

    
    public Container getHostages() {
        return hostages;
    }

    
    public void setHostages(Container hostages) {
        this.hostages = hostages;
    }

    
    public String getNationName() {
        return nationName;
    }

    
    public void setNationName(String nationName) {
        this.nationName = nationName;
    }

    
    public Container getSnas() {
        return snas;
    }

    
    public void setSnas(Container snas) {
        this.snas = snas;
    }

    
    public Container getArtifacts() {
        return artifacts;
    }

    
    public void setArtifacts(Container artifacts) {
        this.artifacts = artifacts;
    }

    
    public Container getAnchoredShips() {
        return anchoredShips;
    }

    
    public void setAnchoredShips(Container anchoredShips) {
        this.anchoredShips = anchoredShips;
    }
    
    
    
}
