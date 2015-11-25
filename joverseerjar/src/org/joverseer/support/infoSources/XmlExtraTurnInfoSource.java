package org.joverseer.support.infoSources;

import org.joverseer.support.GameHolder;

public class XmlExtraTurnInfoSource extends TurnInfoSource {
	private static final long serialVersionUID = -8972028170953714692L;
	int nationNo;

    public XmlExtraTurnInfoSource(int turnNo, int nationNo) {
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
    	return "XMLe (" + GameHolder.instance().getGame().getMetadata().getNationByNum(getNationNo()).getShortName() + ")";
    }

}
