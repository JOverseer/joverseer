package org.joverseer.support.infoSources;


public class DoubleAgentInfoSource extends InfoSource {
    /**
     * 
     */
    private static final long serialVersionUID = 566193776197455349L;
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

    public String getDescription() {
        return String.valueOf(nationNo);
    }
}
