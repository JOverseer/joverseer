package org.joverseer.support.readers.xml;

import java.util.ArrayList;
import org.joverseer.domain.Character;
import org.joverseer.domain.SpellProficiency;
import org.joverseer.domain.InformationSourceEnum;


public class CharacterWrapper {
    String id;
    String name;
    int location = -1;
    int nation;
    int command;
    int totalCommand;
    int agent;
    int totalAgent;
    int mage;
    int totalMage;
    int emmisary;
    int totalEmmisary;
    int stealth;
    int totalStealth;
    int challenge;
    int health;
    String title;
    int informationSource;
    ArrayList artifacts = new ArrayList();
    ArrayList spells = new ArrayList();

    public int getAgent() {
        return agent;
    }

    public void setAgent(int agent) {
        this.agent = agent;
    }

    public ArrayList getArtifacts() {
        return artifacts;
    }

    public void setArtifacts(ArrayList artifacts) {
        this.artifacts = artifacts;
    }

    public int getChallenge() {
        return challenge;
    }

    public void setChallenge(int challenge) {
        this.challenge = challenge;
    }

    public int getCommand() {
        return command;
    }

    public void setCommand(int command) {
        this.command = command;
    }

    public int getEmmisary() {
        return emmisary;
    }

    public void setEmmisary(int emmisary) {
        this.emmisary = emmisary;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getInformationSource() {
        return informationSource;
    }

    public void setInformationSource(int informationSource) {
        this.informationSource = informationSource;
    }

    public int getLocation() {
        return location;
    }

    public void setLocation(int location) {
        this.location = location;
    }

    public int getMage() {
        return mage;
    }

    public void setMage(int mage) {
        this.mage = mage;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNation() {
        return nation;
    }

    public void setNation(int nation) {
        this.nation = nation;
    }

    public ArrayList getSpells() {
        return spells;
    }

    public void setSpells(ArrayList spells) {
        this.spells = spells;
    }

    public int getStealth() {
        return stealth;
    }

    public void setStealth(int stealth) {
        this.stealth = stealth;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getTotalAgent() {
        return totalAgent;
    }

    public void setTotalAgent(int totalAgent) {
        this.totalAgent = totalAgent;
    }

    public int getTotalCommand() {
        return totalCommand;
    }

    public void setTotalCommand(int totalCommand) {
        this.totalCommand = totalCommand;
    }

    public int getTotalEmmisary() {
        return totalEmmisary;
    }

    public void setTotalEmmisary(int totalEmmisary) {
        this.totalEmmisary = totalEmmisary;
    }

    public int getTotalMage() {
        return totalMage;
    }

    public void setTotalMage(int totalMage) {
        this.totalMage = totalMage;
    }

    public int getTotalStealth() {
        return totalStealth;
    }

    public void setTotalStealth(int totalStealth) {
        this.totalStealth = totalStealth;
    }

    public void addArtifact(String artifact) {
        artifacts.add(artifact);
    }

    public void addSpell(String spell) {
        spells.add(spell);
    }

    public Character getCharacter() {
        Character character = new Character();
        character.setId(Character.getIdFromName(getName()));
        character.setName(getName());
        character.setNationNo(getNation());
        character.setX(getLocation() / 100);
        character.setY(getLocation() % 100);
        character.setTitle(getTitle());
        character.setCommand(getCommand());
        character.setCommandTotal(getTotalCommand());
        character.setAgent(getAgent());
        character.setAgentTotal(getTotalAgent());
        character.setEmmisary(getEmmisary());
        character.setEmmisaryTotal(getTotalEmmisary());
        character.setMage(getMage());
        character.setMageTotal(getTotalMage());
        character.setStealth(getStealth());
        character.setStealthTotal(getTotalStealth());
        character.setChallenge(getChallenge());
        if (getHealth() > 0) {
            character.setHealth(getHealth());
        }

        String artifactId;
        ArrayList<Integer> artifacts = new ArrayList<Integer>();
        for (String artifact : (ArrayList<String>)getArtifacts()) {
            int i = artifact.indexOf(' ');
            artifactId = artifact.substring(1, i);
            artifacts.add(Integer.parseInt(artifactId));
        }
        character.setArtifacts(artifacts);

        String spellId;
        String proficiency;
        String name;
        ArrayList<SpellProficiency> spells = new ArrayList<SpellProficiency>();
        for (String spell : (ArrayList<String>)getSpells()) {
            int i = spell.indexOf(' ');
            spellId = spell.substring(1, i);
            int idx1 = spell.indexOf("(");
            int idx2 = spell.indexOf(")");
            proficiency = spell.substring(idx1+1, idx2);
            name = spell.substring(i+1, idx1);
            spells.add(new SpellProficiency(Integer.parseInt(spellId), Integer.parseInt(proficiency), name));
        }
        character.setSpells(spells);
        switch (getInformationSource()) {
            case 0:
                character.setInformationSource(InformationSourceEnum.exhaustive);
                break;
            case 1:
                character.setInformationSource(InformationSourceEnum.detailed);
                break;
            case 2:
                character.setInformationSource(InformationSourceEnum.some);
                break;
            case 3:
                character.setInformationSource(InformationSourceEnum.some);
                break;
            case 4:
                character.setInformationSource(InformationSourceEnum.limited);
                break;
        }
        
        if (getInformationSource() == 0) {
            character.setHostage(getLocation() == 0);
        } 
        return character;
    }
}

