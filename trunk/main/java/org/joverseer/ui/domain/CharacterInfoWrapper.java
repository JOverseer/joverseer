package org.joverseer.ui.domain;

import org.joverseer.domain.IBelongsToNation;
import org.joverseer.domain.IHasMapLocation;
import org.joverseer.domain.IHasTurnNumber;


public class CharacterInfoWrapper implements IHasMapLocation, IHasTurnNumber, IBelongsToNation {
    String name;
    Integer nationNo;
    int hexNo;
    
    Integer command;
    Integer agent;
    Integer emissary;
    Integer mage;
    
    int health;
    int stealth;
    int challenge;
    
    int turnNo;
    int firstTurnNo;
    
    String sources;
    
    boolean startingChar;
    
    String artifacts;

    
    public Integer getAgent() {
        return agent;
    }

    
    public void setAgent(Integer agent) {
        this.agent = agent;
    }

    
    public String getArtifacts() {
        return artifacts;
    }

    
    public void setArtifacts(String artifacts) {
        this.artifacts = artifacts;
    }

    
    public Integer getCommand() {
        return command;
    }

    
    public void setCommand(Integer command) {
        this.command = command;
    }

    
    public Integer getEmissary() {
        return emissary;
    }

    
    public void setEmissary(Integer emissary) {
        this.emissary = emissary;
    }

    
    public int getHexNo() {
        return hexNo;
    }

    
    public void setHexNo(int hexNo) {
        this.hexNo = hexNo;
    }

    
    public Integer getMage() {
        return mage;
    }

    
    public void setMage(Integer mage) {
        this.mage = mage;
    }

    
    public String getName() {
        return name;
    }

    
    public void setName(String name) {
        this.name = name;
    }

    
    public Integer getNationNo() {
        return nationNo;
    }

    
    public void setNationNo(Integer nationNo) {
        this.nationNo = nationNo;
    }

    
    public String getSources() {
        return sources;
    }

    
    public void setSources(String sources) {
        this.sources = sources;
    }

    
    public boolean isStartingChar() {
        return startingChar;
    }

    
    public void setStartingChar(boolean startingChar) {
        this.startingChar = startingChar;
    }

    
    public int getTurnNo() {
        return turnNo;
    }

    
    public void setTurnNo(int turnNo) {
        this.turnNo = turnNo;
    }


    
    public int getHealth() {
        return health;
    }


    
    public void setHealth(int health) {
        this.health = health;
    }

    public int getChallenge() {
        return challenge;
    }
    
    public void setChallenge(int challenge) {
        this.challenge = challenge;
    }
    
    public int getStealth() {
        return stealth;
    }


    
    public void setStealth(int stealth) {
        this.stealth = stealth;
    }


    public int getX() {
        return getHexNo() / 100;
    }


    public int getY() {
        return getHexNo() % 100;
    }


    
    public int getFirstTurnNo() {
        return firstTurnNo;
    }


    
    public void setFirstTurnNo(int firstTurnNo) {
        this.firstTurnNo = firstTurnNo;
    }
    
    
}
