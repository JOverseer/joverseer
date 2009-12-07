package org.joverseer.support.readers.newXml;

public class ArtifactWrapper {
	int id;
	String name;
	int mage;
	int command;
	int emissary;
	int agent;
	int stealth;
	int combat;
	String alignment;
	String latent;
	String item;
	
	public int getAgent() {
		return agent;
	}
	public void setAgent(int agent) {
		this.agent = agent;
	}
	public String getAlignment() {
		return alignment;
	}
	public void setAlignment(String alignment) {
		this.alignment = alignment;
	}
	public int getCombat() {
		return combat;
	}
	public void setCombat(int combat) {
		this.combat = combat;
	}
	public int getCommand() {
		return command;
	}
	public void setCommand(int command) {
		this.command = command;
	}
	public int getEmissary() {
		return emissary;
	}
	public void setEmissary(int emissary) {
		this.emissary = emissary;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getItem() {
		return item;
	}
	public void setItem(String item) {
		this.item = item;
	}
	public String getLatent() {
		return latent;
	}
	public void setLatent(String latent) {
		this.latent = latent;
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
	public int getStealth() {
		return stealth;
	}
	public void setStealth(int stealth) {
		this.stealth = stealth;
	}
	
	public String getPower() {
		if (command > 0) {
			return "Command " + command;
		} else if (agent > 0) {
			return "Agent " + agent;
		} else if (emissary > 0) {
			return "Emissary " + emissary;
		} else if (mage > 0) {
			return "Mage " + mage;
		} else if (stealth > 0) {
			return "Stealth " + stealth;
		} else if (combat > 0) {
			return "Combat " + combat * 50;
		}
		return "";
	}
	
}
