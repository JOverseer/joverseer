package org.joverseer.domain;

import org.joverseer.domain.structuredOrderResults.IStructuredOrderResult;
import org.joverseer.metadata.domain.Nation;
import org.joverseer.support.AsciiUtils;
import org.joverseer.support.NationMap;
import org.joverseer.support.infoSources.InfoSource;

import java.util.ArrayList;
import java.io.Serializable;
import java.lang.reflect.Method;

import sun.text.Normalizer;

/**
 * Stores information about a character reported in the turn results.
 * 
 * @author Marios Skounakis
 *
 */
public class Character implements IBelongsToNation, IHasMapLocation, IMaintenanceCost, Serializable {

    private static final long serialVersionUID = 2372359979734224557L;
    String id;
    String name;
    String title;

    Integer nationNo;

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
    Integer health;
    InfoSourceValue healthEstimate;
    ArrayList<IStructuredOrderResult> structuredOrderResults = new ArrayList<IStructuredOrderResult>();
    
    int x;
    int y;
    
    Boolean isHostage = null;

    ArrayList<Integer> artifacts = new ArrayList<Integer>();
    ArrayList<SpellProficiency> spells = new ArrayList<SpellProficiency>();
    ArrayList<String> hostages = new ArrayList<String>();
    int artifactInUse;
    
    InformationSourceEnum informationSource;
    InfoSource infoSource;

    Order[] orders = new Order[]{new Order(this), new Order(this), new Order(this)};
    
    int numberOfOrders = 2;
    
    String orderResults;
    String encounter;
    
   
    CharacterDeathReasonEnum deathReason = CharacterDeathReasonEnum.NotDead;

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

    public Integer getHealth() {
        return health;
    }

    public void setHealth(Integer health) {
        this.health = health;
    }
    
    

    public InfoSourceValue getHealthEstimate() {
		return healthEstimate;
	}

	public void setHealthEstimate(InfoSourceValue healthEstimate) {
		this.healthEstimate = healthEstimate;
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

    public ArrayList<SpellProficiency> getSpells() {
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

    public Integer getNationNo() {
        return nationNo;
    }

    public void setNationNo(Integer nationNo) {
        this.nationNo = nationNo;
    }

    public int getHexNo() {
        return getX() * 100 + getY();
    }

    public void setHexNo(int hexNo) {
        setX(hexNo / 100);
        setY(hexNo % 100);
    }

    public Order[] getOrders() {
    	if (orders.length == 2) {
    		orders = new Order[]{orders[0], orders[1], new Order(this)};
    	}
        return orders;
    }

    public void setOrders(Order[] orders) {
        this.orders = orders;
    }

    public static String getIdFromName(String name) {
        String id = name.toLowerCase().substring(0, Math.min(5, name.length()));
        return AsciiUtils.convertNonAscii(id);
    }
    
    public static String getSpacePaddedIdFromId(String id) {
    	return id + "     ".substring(0, 5 - id.length());
    }

    
    public String getEncounter() {
        return encounter;
    }

    
    public void setEncounter(String encounter) {
        this.encounter = encounter;
    }

    
    public String getOrderResults() {
        return orderResults;
    }

    
    public void setOrderResults(String orderResults) {
        this.orderResults = orderResults;
    }

    
    public CharacterDeathReasonEnum getDeathReason() {
        return deathReason;
    }

    
    public void setDeathReason(CharacterDeathReasonEnum deathReason) {
        this.deathReason = deathReason;
    }
    
    public static void main(String[] args) {
        System.out.println(Character.getIdFromName("Michèle"));
    }
    
    public Boolean getHostage() {
        return isHostage;
    }
    
    public void setHostage(Boolean isHostage) {
        this.isHostage = isHostage;
    }

    
    public int getArtifactInUse() {
        return artifactInUse;
    }

    
    public void setArtifactInUse(int artifactInUse) {
        this.artifactInUse = artifactInUse;
    }
    
    public Nation getNation() {
        return NationMap.getNationFromNo(getNationNo());
    }    
    
    public void setNation(Nation nation) {
        setNationNo(nation.getNumber());
    }
    
    public ArrayList<IStructuredOrderResult> getStructuredOrderResults() {
    	if (structuredOrderResults == null) {
    		structuredOrderResults = new ArrayList<IStructuredOrderResult>();
    	}
    	return structuredOrderResults;
    }

    
    public ArrayList<String> getHostages() {
        if (hostages == null) {
            hostages = new ArrayList<String>();
        }
        return hostages;
    }

    
    public void setHostages(ArrayList<String> hostages) {
        this.hostages = hostages;
    }

	public int getNumberOfOrders() {
		if (numberOfOrders == 0) numberOfOrders = 2;
		return numberOfOrders;
	}

	public void setNumberOfOrders(int numberOfOrders) {
		this.numberOfOrders = numberOfOrders;
	}

	public Integer getMaintenance() {
		return (getCommand() + getMage() + getAgent() + getEmmisary()) * 20;
	}

	
	
}
