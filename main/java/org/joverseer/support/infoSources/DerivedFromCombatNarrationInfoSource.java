package org.joverseer.support.infoSources;

/**
 * Information derived from combat narrations.
 * 
 * TODO: Check if this is used anywhere
 * 
 * @author Marios Skounakis
 *
 */
public class DerivedFromCombatNarrationInfoSource extends PdfTurnInfoSource {
	private static final long serialVersionUID = -5626983766267969103L;
	String lossesDescription;

	public DerivedFromCombatNarrationInfoSource(int turnNo, int nationNo) {
		super(turnNo, nationNo);
	}

	public String getLossesDescription() {
		return this.lossesDescription;
	}

	public void setLossesDescription(String lossesDescription) {
		this.lossesDescription = lossesDescription;
	}
	
	@Override
	public String toString() {
    	return "Combat narration";
    }
}
