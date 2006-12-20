package org.joverseer.support.infoSources;

import org.joverseer.domain.InformationSourceEnum;


public class XmlTurnInfoSource extends TurnInfoSource {
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
