package org.joverseer.support.infoSources;

import org.joverseer.support.GameHolder;

/**
 * Information derived from a pdf turn file
 * 
 * @author Marios Skounakis
 *
 */
public class PdfTurnInfoSource extends TurnInfoSource {
    private static final long serialVersionUID = 130686164691200861L;
	int nationNo;

    public PdfTurnInfoSource(int turnNo, int nationNo) {
        this.nationNo = nationNo;
        setTurnNo(turnNo);
    }

    public int getNationNo() {
        return this.nationNo;
    }

    public void setNationNo(int nationNo) {
        this.nationNo = nationNo;
    }

    @Override
	public String toString() {
    	return "PDF (" + GameHolder.instance().getGame().getMetadata().getNationByNum(getNationNo()).getShortName() + ")";
    }
    
}
