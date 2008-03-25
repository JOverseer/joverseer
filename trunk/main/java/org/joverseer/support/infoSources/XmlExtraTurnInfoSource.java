package org.joverseer.support.infoSources;

public class XmlExtraTurnInfoSource extends TurnInfoSource {
	private static final long serialVersionUID = -8972028170953714692L;
	int nationNo;

    public XmlExtraTurnInfoSource(int turnNo, int nationNo) {
        this.nationNo = nationNo;
        setTurnNo(turnNo);
    }

    public int getNationNo() {
        return nationNo;
    }

    public void setNationNo(int nationNo) {
        this.nationNo = nationNo;
    }
}
