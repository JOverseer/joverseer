package org.joverseer.support.readers.newXml;

import java.util.ArrayList;

public class BattleWrapper {
	String text;
	ArrayList<String> lines = new ArrayList<String>();
	ArrayList<BattleLine> battleLines = new ArrayList<BattleLine>();
	int hexNo;
	
	
	
	public int getHexNo() {
		return hexNo;
	}

	public void setHexNo(int hexNo) {
		this.hexNo = hexNo;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
	
	public void addLine(BattleLine line) {
		battleLines.add(line);
	}
	
	
	
	public void parse() {
		for (BattleLine line : battleLines) {
			String txt = line.getText(); 
			for (int i=0; i<line.getTroopTypes().size(); i++) {
				txt += (txt.equals("") ? "" : "\n") + line.getTroopTypes().get(i) + " with " + line.getWeaponTypes().get(i) + " weapons, " + line.getArmors().get(i) + " armor, " + line.getFormations().get(i);
			}
			for (int i=0; i<line.getCommanderReports().size(); i++) {
				txt += (txt.equals("") ? "" : "\n") + line.getCommanderReports().get(i);
			}
			for (int i=0; i<line.getSummaryReports().size(); i++) {
				txt += (txt.equals("") ? "" : "\n") + line.getSummaryReports().get(i);
			}
			if (getText() == null) {
				setText("");
			}
			setText(getText() + (getText().equals("") ? "" : "\n") + txt);
			lines.add(txt);
		}
		if (lines.size() == 0) return;
		String first = lines.get(0).trim();
		if (first.startsWith("Battle at ")) {
			String hex = first.substring(first.length() - 4, first.length());
			setHexNo(Integer.parseInt(hex));
		}
	}
}
