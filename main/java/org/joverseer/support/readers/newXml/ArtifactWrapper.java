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
		return this.agent;
	}
	public void setAgent(int agent) {
		this.agent = agent;
	}
	public String getAlignment() {
		return this.alignment;
	}
	public void setAlignment(String alignment) {
		this.alignment = alignment;
	}
	public int getCombat() {
		return this.combat;
	}
	public void setCombat(int combat) {
		this.combat = combat;
	}
	public int getCommand() {
		return this.command;
	}
	public void setCommand(int command) {
		this.command = command;
	}
	public int getEmissary() {
		return this.emissary;
	}
	public void setEmissary(int emissary) {
		this.emissary = emissary;
	}
	public int getId() {
		return this.id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getItem() {
		return this.item;
	}
	public void setItem(String item) {
		this.item = item;
	}
	public String getLatent() {
		return this.latent;
	}
	public void setLatent(String latent) {
		this.latent = latent;
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
	public int getStealth() {
		return this.stealth;
	}
	public void setStealth(int stealth) {
		this.stealth = stealth;
	}
	
	public String getPower() {
		if (this.command > 0) {
			return "Command " + this.command;
		} else if (this.agent > 0) {
			return "Agent " + this.agent;
		} else if (this.emissary > 0) {
			return "Emissary " + this.emissary;
		} else if (this.mage > 0) {
			return "Mage " + this.mage;
		} else if (this.stealth > 0) {
			return "Stealth " + this.stealth;
		} else if (this.combat > 0) {
			return "Combat " + this.combat * 50;
		}
		return "";
	}
	
}
