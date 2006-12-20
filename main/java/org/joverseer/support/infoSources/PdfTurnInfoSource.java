package org.joverseer.support.infoSources;


public class PdfTurnInfoSource extends TurnInfoSource {
    int nationNo;

    public PdfTurnInfoSource(int turnNo, int nationNo) {
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
