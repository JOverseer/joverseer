package org.joverseer.support.readers.xml;

import java.util.ArrayList;

import org.joverseer.domain.Character;
import org.joverseer.domain.CharacterDeathReasonEnum;
import org.joverseer.domain.InformationSourceEnum;
import org.joverseer.domain.SpellProficiency;

/**
 * Holds information about characters (from xml turns)
 * 
 * @author Marios Skounakis
 */
public class CharacterWrapper {
	String id;
	String name;
	String location = null;
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
	int ordersAllowed;
	ArrayList<String> artifacts = new ArrayList<String>();
	ArrayList<String> spells = new ArrayList<String>();

	public int getAgent() {
		return this.agent;
	}

	public void setAgent(int agent) {
		this.agent = agent;
	}

	public ArrayList<String> getArtifacts() {
		return this.artifacts;
	}

	public void setArtifacts(ArrayList<String> artifacts) {
		this.artifacts = artifacts;
	}

	public int getChallenge() {
		return this.challenge;
	}

	public void setChallenge(int challenge) {
		this.challenge = challenge;
	}

	public int getCommand() {
		return this.command;
	}

	public void setCommand(int command) {
		this.command = command;
	}

	public int getEmmisary() {
		return this.emmisary;
	}

	public void setEmmisary(int emmisary) {
		this.emmisary = emmisary;
	}

	public int getHealth() {
		return this.health;
	}

	public void setHealth(int health) {
		this.health = health;
	}

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getInformationSource() {
		return this.informationSource;
	}

	public void setInformationSource(int informationSource) {
		this.informationSource = informationSource;
	}

	public String getLocation() {
		return this.location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public int getMage() {
		return this.mage;
	}

	public void setMage(int mage) {
		this.mage = mage;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getNation() {
		return this.nation;
	}

	public void setNation(int nation) {
		this.nation = nation;
	}

	public ArrayList<String> getSpells() {
		return this.spells;
	}

	public void setSpells(ArrayList<String> spells) {
		this.spells = spells;
	}

	public int getStealth() {
		return this.stealth;
	}

	public void setStealth(int stealth) {
		this.stealth = stealth;
	}

	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getTotalAgent() {
		return this.totalAgent;
	}

	public void setTotalAgent(int totalAgent) {
		this.totalAgent = totalAgent;
	}

	public int getTotalCommand() {
		return this.totalCommand;
	}

	public void setTotalCommand(int totalCommand) {
		this.totalCommand = totalCommand;
	}

	public int getTotalEmmisary() {
		return this.totalEmmisary;
	}

	public void setTotalEmmisary(int totalEmmisary) {
		this.totalEmmisary = totalEmmisary;
	}

	public int getTotalMage() {
		return this.totalMage;
	}

	public void setTotalMage(int totalMage) {
		this.totalMage = totalMage;
	}

	public int getTotalStealth() {
		return this.totalStealth;
	}

	public void setTotalStealth(int totalStealth) {
		this.totalStealth = totalStealth;
	}

	public int getOrdersAllowed() {
		return this.ordersAllowed;
	}

	public void setOrdersAllowed(int ordersAllowed) {
		this.ordersAllowed = ordersAllowed;
	}

	public void addArtifact(String artifact) {
		this.artifacts.add(artifact);
	}

	public void addSpell(String spell) {
		this.spells.add(spell);
	}

	public Character getCharacter() {
		Character character = new Character();
		character.setId(Character.getIdFromName(getName()));
		character.setName(getName());
		character.setNationNo(getNation());
		int hexNo = 0;
		try {
			hexNo = Integer.parseInt(getLocation());
		} catch (Exception exc) {
			if (getLocation().equals("DEAD")) {
				hexNo = 0;
				setHealth(0);
			}
		}
		character.setX(hexNo / 100);
		character.setY(hexNo % 100);
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
		} else if (hexNo == 0) {
			// dead
			character.setDeathReason(CharacterDeathReasonEnum.Dead);
		}
		character.setNumberOfOrders(getOrdersAllowed());
		String artifactId;
		ArrayList<Integer> artifacts1 = new ArrayList<Integer>();
		for (String artifact : getArtifacts()) {
			int i = artifact.indexOf(' ');
			artifactId = artifact.substring(1, i);
			artifacts1.add(Integer.parseInt(artifactId));
		}
		character.setArtifacts(artifacts1);

		String spellId;
		String proficiency;
		String name1;
		ArrayList<SpellProficiency> spells1 = new ArrayList<SpellProficiency>();
		for (String spell : getSpells()) {
			int i = spell.indexOf(' ');
			spellId = spell.substring(1, i);
			int idx1 = spell.indexOf("(");
			int idx2 = spell.indexOf(")");
			proficiency = spell.substring(idx1 + 1, idx2);
			name1 = spell.substring(i + 1, idx1);
			spells1.add(new SpellProficiency(Integer.parseInt(spellId), Integer.parseInt(proficiency), name1));
		}
		character.setSpells(spells1);
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

		if (getInformationSource() == 0 && !getLocation().equals("DEAD")) {
			character.setHostage(getLocation().equals("HOST") || "0".equals(getLocation()) || "0000".equals(getLocation()));
		}
		return character;
	}
}
