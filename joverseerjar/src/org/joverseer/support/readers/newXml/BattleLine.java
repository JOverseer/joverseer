package org.joverseer.support.readers.newXml;

import java.util.ArrayList;

public class BattleLine {
	String text;
	ArrayList<String> troopTypes = new ArrayList<String>();
	ArrayList<String> weaponTypes = new ArrayList<String>();
	ArrayList<String> armors = new ArrayList<String>();
	ArrayList<String> formations = new ArrayList<String>();
	ArrayList<String> commanderReports = new ArrayList<String>();
	ArrayList<String> summaryReports = new ArrayList<String>();
	
	
	
	public void setSummaryReport(String summaryReport) {
		this.summaryReports.add(summaryReport);
	}

	public void setCommanderReport(String commanderReport) {
		this.commanderReports.add(commanderReport);
	}

	public String getText() {
		return this.text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public void setTroopType(String troopType) {
		this.troopTypes.add(troopType);
	}

	public void setWeaponType(String weaponType) {
		this.weaponTypes.add(weaponType);
	}

	public void setArmor(String armor) {
		this.armors.add(armor);
	}

	public void setFormation(String formations) {
		this.formations.add(formations);
	}

	public ArrayList<String> getTroopTypes() {
		return this.troopTypes;
	}

	public ArrayList<String> getWeaponTypes() {
		return this.weaponTypes;
	}

	public ArrayList<String> getArmors() {
		return this.armors;
	}

	public ArrayList<String> getFormations() {
		return this.formations;
	}

	public ArrayList<String> getCommanderReports() {
		return this.commanderReports;
	}

	public ArrayList<String> getSummaryReports() {
		return this.summaryReports;
	}
	
	
}
