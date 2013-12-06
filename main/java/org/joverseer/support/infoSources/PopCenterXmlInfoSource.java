package org.joverseer.support.infoSources;

import java.io.Serializable;

/**
 * Specialized info source for Pop Centers read from xml files
 * It also stores the "previous" turn number, which applies only to non-friendly pops, for
 * which info such as the owner may be coming from an older turn.
 * 
 * @author Marios Skounakis
 *
 */
public class PopCenterXmlInfoSource extends XmlTurnInfoSource implements Serializable {
    private static final long serialVersionUID = -5802742503205388210L;
	int previousTurnNo;

    
    public PopCenterXmlInfoSource(int turnNo, int nationNo, int previousTurnNo) {
        super(turnNo, nationNo);
        this.previousTurnNo = previousTurnNo;
    }


    public int getPreviousTurnNo() {
        return this.previousTurnNo;
    }

    
    public void setPreviousTurn(int previousTurnNo) {
        this.previousTurnNo = previousTurnNo;
    }
    
    
}
