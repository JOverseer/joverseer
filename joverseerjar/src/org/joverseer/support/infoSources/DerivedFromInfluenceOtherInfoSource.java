package org.joverseer.support.infoSources;

/**
 * Information derived from an Influence Other order report. Contains:
 * - the name of the character
 * - the description for the pop center loyalty
 * 
 * @author Marios Skounakis
 *
 */
public class DerivedFromInfluenceOtherInfoSource extends PdfTurnInfoSource {
	private static final long serialVersionUID = 2062328224898303323L;
	String charName;
	String loyaltyResult;

	public String getLoyaltyResult() {
		return this.loyaltyResult;
	}

	public void setLoyaltyResult(String loyaltyResult) {
		this.loyaltyResult = loyaltyResult;
	}

	public String getCharName() {
		return this.charName;
	}

	public void setCharName(String charName) {
		this.charName = charName;
	}

	public DerivedFromInfluenceOtherInfoSource(int turnNo, int nationNo, String charName, String loyaltyResult) {
		super(turnNo, nationNo);
		this.charName = charName;
		this.loyaltyResult = loyaltyResult;
	}
	
	@Override
	public String toString() {
    	return "InfOther Rumor";
    }
}
