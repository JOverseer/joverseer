package org.joverseer.support.infoSources;

/**
 * Information about the character's wounds.
 * 
 * @author Marios Skounakis
 *
 */
public class DerivedFromWoundsInfoSource extends PdfTurnInfoSource {

	private static final long serialVersionUID = 8782783582422616900L;
	String woundsDescription;

	public String getWoundsDescription() {
		return this.woundsDescription;
	}

	public void setWoundsDescription(String woundsDescription) {
		this.woundsDescription = woundsDescription;
	}

	public DerivedFromWoundsInfoSource(int turnNo, int nationNo) {
		super(turnNo, nationNo);
	}
	
	@Override
	public String toString() {
    	return "Wounds";
    }

}
