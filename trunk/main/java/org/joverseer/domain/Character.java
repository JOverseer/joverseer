package org.joverseer.domain;

import org.joverseer.support.infoSources.InfoSource;

import java.util.ArrayList;
import java.io.Serializable;

import sun.text.Normalizer;

/**
 * Created by IntelliJ IDEA.
 * User: mskounak
 * Date: Sep 17, 2006
 * Time: 9:01:45 PM
 * To change this template use File | Settings | File Templates.
 */
public class Character implements IBelongsToNation, IHasMapLocation, Serializable {
    String id;
    String name;
    String title;

    int nationNo;

    int command;
    int commandTotal;
    int agent;
    int agentTotal;
    int emmisary;
    int emmisaryTotal;
    int mage;
    int mageTotal;
    int stealth;
    int stealthTotal;
    int challenge;
    int health;

    int x;
    int y;

    ArrayList<Integer> artifacts = new ArrayList<Integer>();
    ArrayList<SpellProficiency> spells = new ArrayList<SpellProficiency>();

    InformationSourceEnum informationSource;
    InfoSource infoSource;

    public int getAgent() {
        return agent;
    }

    public void setAgent(int agent) {
        this.agent = agent;
    }

    public int getAgentTotal() {
        return agentTotal;
    }

    public void setAgentTotal(int agentTotal) {
        this.agentTotal = agentTotal;
    }

    public ArrayList<Integer> getArtifacts() {
        return artifacts;
    }

    public void setArtifacts(ArrayList<Integer> artifacts) {
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

    public int getCommandTotal() {
        return commandTotal;
    }

    public void setCommandTotal(int commandTotal) {
        this.commandTotal = commandTotal;
    }

    public int getEmmisary() {
        return emmisary;
    }

    public void setEmmisary(int emmisary) {
        this.emmisary = emmisary;
    }

    public int getEmmisaryTotal() {
        return emmisaryTotal;
    }

    public void setEmmisaryTotal(int emmisaryTotal) {
        this.emmisaryTotal = emmisaryTotal;
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

    public InfoSource getInfoSource() {
        return infoSource;
    }

    public void setInfoSource(InfoSource infoSource) {
        this.infoSource = infoSource;
    }

    public int getMage() {
        return mage;
    }

    public void setMage(int mage) {
        this.mage = mage;
    }

    public int getMageTotal() {
        return mageTotal;
    }

    public void setMageTotal(int mageTotal) {
        this.mageTotal = mageTotal;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public int getStealthTotal() {
        return stealthTotal;
    }

    public void setStealthTotal(int stealthTotal) {
        this.stealthTotal = stealthTotal;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public InformationSourceEnum getInformationSource() {
        return informationSource;
    }

    public void setInformationSource(InformationSourceEnum informationSource) {
        this.informationSource = informationSource;
    }

    public int getNationNo() {
        return nationNo;
    }

    public void setNationNo(int nationNo) {
        this.nationNo = nationNo;
    }

    public String getHexNo() {
        return String.valueOf(getX() * 100 + getY());
    }

    public void setHexNo(String hexNo) {
        int hexN = Integer.parseInt(hexNo);
        setX(hexN / 100);
        setY(hexN % 100);
    }

    public static String getIdFromName(String name) {
        String id = name.toLowerCase().substring(0, Math.min(5, name.length()));
        id = Normalizer.normalize(id, Normalizer.DECOMP, 0);
        return id.replaceAll("[^\\p{ASCII}]", "");
    }
}
