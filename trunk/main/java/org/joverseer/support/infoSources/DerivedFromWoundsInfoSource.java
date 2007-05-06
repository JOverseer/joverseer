package org.joverseer.support.infoSources;

public class DerivedFromWoundsInfoSource extends PdfTurnInfoSource {
	String woundsDescription;

	public String getWoundsDescription() {
		return woundsDescription;
	}

	public void setWoundsDescription(String woundsDescription) {
		this.woundsDescription = woundsDescription;
	}

	public DerivedFromWoundsInfoSource(int turnNo, int nationNo) {
		super(turnNo, nationNo);
	}
	
	

}
