package org.joverseer.support.infoSources;

public class DerivedFromCombatNarrationInfoSource extends PdfTurnInfoSource {
	String lossesDescription;

	public DerivedFromCombatNarrationInfoSource(int turnNo, int nationNo) {
		super(turnNo, nationNo);
	}

	public String getLossesDescription() {
		return lossesDescription;
	}

	public void setLossesDescription(String lossesDescription) {
		this.lossesDescription = lossesDescription;
	}
	
}
