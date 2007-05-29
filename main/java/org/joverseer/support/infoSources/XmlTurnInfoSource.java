package org.joverseer.support.infoSources;

import org.joverseer.domain.InformationSourceEnum;

/**
 * Information extracted from an xml turn file
 * 
 * @author Marios Skounakis
 *
 */
public class XmlTurnInfoSource extends TurnInfoSource {
    private static final long serialVersionUID = -5522132338476383006L;
	int nationNo;

    public XmlTurnInfoSource(int turnNo, int nationNo) {
        this.nationNo = nationNo;
        this.turnNo = turnNo;
    }

    public int getNationNo() {
        return nationNo;
    }

    public void setNationNo(int nationNo) {
        this.nationNo = nationNo;
    }

}
