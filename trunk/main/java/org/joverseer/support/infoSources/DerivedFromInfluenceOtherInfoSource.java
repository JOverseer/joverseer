package org.joverseer.support.infoSources;

public class DerivedFromInfluenceOtherInfoSource extends PdfTurnInfoSource {
	String charName;
	String loyaltyResult;
	
	

	public String getLoyaltyResult() {
		return loyaltyResult;
	}

	public void setLoyaltyResult(String loyaltyResult) {
		this.loyaltyResult = loyaltyResult;
	}

	public String getCharName() {
		return charName;
	}

	public void setCharName(String charName) {
		this.charName = charName;
	}

	public DerivedFromInfluenceOtherInfoSource(int turnNo, int nationNo, String charName, String loyaltyResult) {
		super(turnNo, nationNo);
		this.charName = charName;
		this.loyaltyResult = loyaltyResult;
	}
	

}
