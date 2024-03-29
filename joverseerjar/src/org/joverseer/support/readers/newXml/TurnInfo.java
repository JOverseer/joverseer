package org.joverseer.support.readers.newXml;

import org.joverseer.support.Container;

public class TurnInfo {
	Container characters;
	Container popCentres;
	Container armies;
	Container hiddenArtifacts;
	Container nonHiddenArtifacts;
	Container hexes;
	Container nationRelations;
	Container companies;
	Container charMessages;
	Container encounters;
	Container snas;
	Container anchoredShips;
	Container battles;
	Container hostages;
	Container recons;
	Container challenges;
	Container doubleAgents;
	Container ordersGiven;
	Container oldPopCentres;
	Container gameInfo;
	Container modifiers;

	public Container getGameInfo() {
		return this.gameInfo;
	}
	public void setGameInfo(Container gameInfo) {
		this.gameInfo = gameInfo;
	}

	int turnNo = -1;
	int nationNo = -1;
	String date;
	String season;
	String nationName;
	boolean seasonChanging;
	int alignment;
	int NPCsRecruited;

	public int getNPCsRecruited() {
		return this.NPCsRecruited;
	}
	public void setNPCsRecruited(int NPCsRecruited) {
		this.NPCsRecruited = NPCsRecruited;
	}

	public int getAlignment() {
		return this.alignment;
	}

	public void setAlignment(int alignment) {
		this.alignment = alignment;
	}

	public Container getDoubleAgents() {
		return this.doubleAgents;
	}

	public void setDoubleAgents(Container doubleAgents) {
		this.doubleAgents = doubleAgents;
	}

	public Container getChallenges() {
		return this.challenges;
	}

	public void setChallenges(Container challenges) {
		this.challenges = challenges;
	}

	public Container getBattles() {
		return this.battles;
	}

	public void setBattles(Container battles) {
		this.battles = battles;
	}

	public Container getAnchoredShips() {
		return this.anchoredShips;
	}

	public void setAnchoredShips(Container anchoredShips) {
		this.anchoredShips = anchoredShips;
	}

	public Container getCharMessages() {
		return this.charMessages;
	}

	public void setCharMessages(Container charMessages) {
		this.charMessages = charMessages;
	}

	public Container getCharacters() {
		return this.characters;
	}

	public void setCharacters(Container characters) {
		this.characters = characters;
	}

	public Container getRecons() {
		return this.recons;
	}

	public void setRecons(Container recons) {
		this.recons = recons;
	}

	public Container getEncounters() {
		return this.encounters;
	}

	public void setEncounters(Container encounters) {
		this.encounters = encounters;
	}

	public Container getArmies() {
		return this.armies;
	}

	public void setArmies(Container armies) {
		this.armies = armies;
	}

	public Container getPopCentres() {
		return this.popCentres;
	}

	public void setPopCentres(Container popCentres) {
		this.popCentres = popCentres;
	}
	public Container getOldPopCentres() {
		return this.oldPopCentres;
	}
	public void setOldPopCentres(Container oldPopCentres) {
		this.oldPopCentres = oldPopCentres;
	}

	public Container getHostages() {
		return this.hostages;
	}

	public void setHostages(Container hostages) {
		this.hostages = hostages;
	}

	public Container getHiddenArtifacts() {
		return this.hiddenArtifacts;
	}

	public void setHiddenArtifacts(Container hiddenArtifacts) {
		this.hiddenArtifacts = hiddenArtifacts;
	}

	public Container getNonHiddenArtifacts() {
		return this.nonHiddenArtifacts;
	}

	public void setNonHiddenArtifacts(Container nonHhiddenArtifacts) {
		this.nonHiddenArtifacts = nonHhiddenArtifacts;
	}

	public Container getHexes() {
		return this.hexes;
	}

	public void setHexes(Container hexes) {
		this.hexes = hexes;
	}

	public String getDate() {
		return this.date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getNationName() {
		return this.nationName;
	}

	public void setNationName(String nationName) {
		this.nationName = nationName;
	}

	public int getNationNo() {
		return this.nationNo;
	}

	public void setNationNo(int nationNo) {
		this.nationNo = nationNo;
	}

	public String getSeason() {
		return this.season;
	}

	public void setSeason(String season) {
		this.season = season;
	}

	public boolean getSeasonChanging() {
		return this.seasonChanging;
	}

	public void setSeasonChanging(boolean seasonChanging) {
		this.seasonChanging = seasonChanging;
	}

	public int getTurnNo() {
		return this.turnNo;
	}

	public void setTurnNo(int turnNo) {
		this.turnNo = turnNo;
	}

	public Container getNationRelations() {
		return this.nationRelations;
	}

	public void setNationRelations(Container nationRelations) {
		this.nationRelations = nationRelations;
	}

	public Container getCompanies() {
		return this.companies;
	}

	public void setCompanies(Container companies) {
		this.companies = companies;
	}

	public Container getSnas() {
		return this.snas;
	}

	public void setSnas(Container snas) {
		this.snas = snas;
	}

	public Container getOrdersGiven() {
		return this.ordersGiven;
	}

	public void setOrdersGiven(Container ordersGiven) {
		this.ordersGiven = ordersGiven;
	}
	
	public Container getModifiers() {
		return this.modifiers;
	}
	public void setModifiers(Container modifiers) {
		this.modifiers = modifiers;	
	}	

}
