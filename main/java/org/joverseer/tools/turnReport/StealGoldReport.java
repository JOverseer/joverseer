package org.joverseer.tools.turnReport;

public class StealGoldReport extends BaseReportObject {
	int gold;
	int stolenFromNation;
	int gainedByNation;
	
	public int getGold() {
		return gold;
	}

	public void setGold(int gold) {
		this.gold = gold;
	}

	public int getStolenFromNation() {
		return stolenFromNation;
	}

	public void setStolenFromNation(int stolenFromNation) {
		this.stolenFromNation = stolenFromNation;
	}

	public int getGainedByNation() {
		return gainedByNation;
	}

	public void setGainedByNation(int gainedByNation) {
		this.gainedByNation = gainedByNation;
	}
	
	public String getHtmlString() {
		return 	
			//appendTd(getNationStr()) +
		//appendTd(getModification()) +
			appendTd(getNationsStr()) +
				appendTd(getHexNoStr()) +
				appendTd(getNotes()) +
				appendTd(getLinks());
		
	}
}
