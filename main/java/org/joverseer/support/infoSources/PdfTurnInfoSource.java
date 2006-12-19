package org.joverseer.support.infoSources;


public class PdfTurnInfoSource extends InfoSource {
    int turnNo;
    int nationNo;

    public PdfTurnInfoSource(int turnNo, int nationNo) {
        this.nationNo = nationNo;
        this.turnNo = turnNo;
    }

    public int getNationNo() {
        return nationNo;
    }

    public void setNationNo(int nationNo) {
        this.nationNo = nationNo;
    }

    public int getTurnNo() {
        return turnNo;
    }

    public void setTurnNo(int turnNo) {
        this.turnNo = turnNo;
    }

}
