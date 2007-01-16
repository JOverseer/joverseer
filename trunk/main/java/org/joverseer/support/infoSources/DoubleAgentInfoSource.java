package org.joverseer.support.infoSources;


public class DoubleAgentInfoSource extends InfoSource {
    int nationNo;

    public DoubleAgentInfoSource(int turnNo, int nationNo) {
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
