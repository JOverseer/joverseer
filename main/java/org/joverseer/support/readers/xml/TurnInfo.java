package org.joverseer.support.readers.xml;

import org.joverseer.support.Container;

/**
 * Holds all the information read from the xml turns
 * 
 * @author Marios Skounakis
 */
public class TurnInfo {
	Container popCentres;
	Container characters;
	Container armies;
	Container nations;

	NationInfoWrapper nationInfoWrapper;
	EconomyWrapper economy;

	int gameNo;
	int turnNo;
	int nationNo;
	String gameType;
	String playerName;
	String accountNo;
	String securityCode;
	String dueDate;
	int nationCapitalHex;

	public Container<CharacterWrapper> getCharacters() {
		return characters;
	}

	public void setCharacters(Container characters) {
		this.characters = characters;
	}

	public Container<PopCenterWrapper> getPopCentres() {
		return popCentres;
	}

	public void setPopCentres(Container popCentres) {
		this.popCentres = popCentres;
	}

	public Container<ArmyWrapper> getArmies() {
		return armies;
	}

	public void setArmies(Container armies) {
		this.armies = armies;
	}

	public Container<NationWrapper> getNations() {
		return nations;
	}

	public void setNations(Container nations) {
		this.nations = nations;
	}

	public int getGameNo() {
		return gameNo;
	}

	public void setGameNo(int gameNo) {
		this.gameNo = gameNo;
	}

	public String getGameType() {
		return gameType;
	}

	public void setGameType(String gameType) {
		this.gameType = gameType;
	}

	public int getNationNo() {
		return nationNo;
	}

	public void setNationNo(int nationNo) {
		this.nationNo = nationNo;
	}

	public int getTurnNo() {
		return turnNo;
	}

	public void setTurnNo(int turnNo) {
		this.turnNo = turnNo;
	}

	public NationInfoWrapper getNationInfoWrapper() {
		return nationInfoWrapper;
	}

	public void setNationInfoWrapper(NationInfoWrapper nationInfoWrapper) {
		this.nationInfoWrapper = nationInfoWrapper;
	}

	public EconomyWrapper getEconomy() {
		return economy;
	}

	public void setEconomy(EconomyWrapper economy) {
		this.economy = economy;
	}

	public String getAccountNo() {
		return accountNo;
	}

	public void setAccountNo(String accountNo) {
		this.accountNo = accountNo;
	}

	public String getDueDate() {
		return dueDate;
	}

	public void setDueDate(String dueDate) {
		this.dueDate = dueDate;
	}

	public String getPlayerName() {
		return playerName;
	}

	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}

	public String getSecurityCode() {
		return securityCode;
	}

	public void setSecurityCode(String secutiryCode) {
		this.securityCode = secutiryCode;
	}

	public int getNationCapitalHex() {
		return nationCapitalHex;
	}

	public void setNationCapitalHex(int nationCapitalHex) {
		this.nationCapitalHex = nationCapitalHex;
	}

}