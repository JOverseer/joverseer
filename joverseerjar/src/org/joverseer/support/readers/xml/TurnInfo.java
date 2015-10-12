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
		return this.characters;
	}

	public void setCharacters(Container characters) {
		this.characters = characters;
	}

	public Container<PopCenterWrapper> getPopCentres() {
		return this.popCentres;
	}

	public void setPopCentres(Container popCentres) {
		this.popCentres = popCentres;
	}

	public Container<ArmyWrapper> getArmies() {
		return this.armies;
	}

	public void setArmies(Container armies) {
		this.armies = armies;
	}

	public Container<NationWrapper> getNations() {
		return this.nations;
	}

	public void setNations(Container nations) {
		this.nations = nations;
	}

	public int getGameNo() {
		return this.gameNo;
	}

	public void setGameNo(int gameNo) {
		this.gameNo = gameNo;
	}

	public String getGameType() {
		return this.gameType;
	}

	public void setGameType(String gameType) {
		this.gameType = gameType;
	}

	public int getNationNo() {
		return this.nationNo;
	}

	public void setNationNo(int nationNo) {
		this.nationNo = nationNo;
	}

	public int getTurnNo() {
		return this.turnNo;
	}

	public void setTurnNo(int turnNo) {
		this.turnNo = turnNo;
	}

	public NationInfoWrapper getNationInfoWrapper() {
		return this.nationInfoWrapper;
	}

	public void setNationInfoWrapper(NationInfoWrapper nationInfoWrapper) {
		this.nationInfoWrapper = nationInfoWrapper;
	}

	public EconomyWrapper getEconomy() {
		return this.economy;
	}

	public void setEconomy(EconomyWrapper economy) {
		this.economy = economy;
	}

	public String getAccountNo() {
		return this.accountNo;
	}

	public void setAccountNo(String accountNo) {
		this.accountNo = accountNo;
	}

	public String getDueDate() {
		return this.dueDate;
	}

	public void setDueDate(String dueDate) {
		this.dueDate = dueDate;
	}

	public String getPlayerName() {
		return this.playerName;
	}

	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}

	public String getSecurityCode() {
		return this.securityCode;
	}

	public void setSecurityCode(String secutiryCode) {
		this.securityCode = secutiryCode;
	}

	public int getNationCapitalHex() {
		return this.nationCapitalHex;
	}

	public void setNationCapitalHex(int nationCapitalHex) {
		this.nationCapitalHex = nationCapitalHex;
	}

}