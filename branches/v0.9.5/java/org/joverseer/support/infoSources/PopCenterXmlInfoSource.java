package org.joverseer.support.infoSources;

import java.io.Serializable;


public class PopCenterXmlInfoSource extends XmlTurnInfoSource implements Serializable {
    int previousTurnNo;

    
    public PopCenterXmlInfoSource(int turnNo, int nationNo, int previousTurnNo) {
        super(turnNo, nationNo);
        this.previousTurnNo = previousTurnNo;
    }


    public int getPreviousTurnNo() {
        return previousTurnNo;
    }

    
    public void setPreviousTurn(int previousTurnNo) {
        this.previousTurnNo = previousTurnNo;
    }
    
    

}
