package org.joverseer.support.infoSources;


public class DoubleAgentInfoSource extends InfoSource {
    private static final long serialVersionUID = 566193776197455349L;
    int nationNo;

    public DoubleAgentInfoSource(int turnNo, int nationNo) {
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
	public String getDescription() {
        return String.valueOf(this.nationNo);
    }
    
    @Override
	public String toString() {
    	return "Double agent";
    }
}
