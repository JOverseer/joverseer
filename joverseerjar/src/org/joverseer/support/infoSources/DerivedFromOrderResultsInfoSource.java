package org.joverseer.support.infoSources;

public class DerivedFromOrderResultsInfoSource extends PdfTurnInfoSource {
	private static final long serialVersionUID = 2072328224898303323L;
	String charName;

	public String getCharName() {
		return this.charName;
	}

	public void setCharName(String charName) {
		this.charName = charName;
	}

	public DerivedFromOrderResultsInfoSource(int turnNo, int nationNo, String charName) {
		super(turnNo, nationNo);
		this.charName = charName;

	}

	@Override
	public String toString() {
		return getCharName() + "'s Order Results";
	}
}
