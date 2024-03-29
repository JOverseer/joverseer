package org.joverseer.support.readers.newXml;

import java.util.ArrayList;

/***
 * 
 * Converts BattleLines to text. and extracts hex no.
 *
 */
public class BattleWrapper {
	String text;
	ArrayList<String> lines = new ArrayList<String>();
	ArrayList<BattleLine> battleLines = new ArrayList<BattleLine>();
	int hexNo;
	
	
	
	public int getHexNo() {
		return this.hexNo;
	}

	public void setHexNo(int hexNo) {
		this.hexNo = hexNo;
	}

	public String getText() {
		return this.text;
	}

	public void setText(String text) {
		this.text = text;
	}
	
	public void addLine(BattleLine line) {
		this.battleLines.add(line);
	}
	
	
	
	public void parse() {
		String report = "";
		for (BattleLine line : this.battleLines) {
			String txt = line.getText(); 
			for (int i=0; i<line.getTroopTypes().size(); i++) {
				txt += (txt.equals("") ? "" : "\n") + "  " + line.getTroopTypes().get(i) + " with " + line.getWeaponTypes().get(i) + " weapons, " + line.getArmors().get(i) + " armor, " + line.getFormations().get(i);
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
			
			report += (report.equals("") ? "" : "\n") + txt;
			this.lines.add(txt);
		}
		while (report.contains("\n\n\n")) {
			report = report.replace("\n\n\n", "\n\n");
		}
		setText(report);
		if (this.lines.size() == 0) return;
		String first = this.lines.get(0).trim();
		if (first.startsWith("Battle at ")) {
			String hex = first.substring(first.length() - 4, first.length());
			setHexNo(Integer.parseInt(hex));
		}
	}
}
