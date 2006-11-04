package org.joverseer.support.infoSources;

import org.joverseer.domain.InformationSourceEnum;

/**
 * Created by IntelliJ IDEA.
 * User: mskounak
 * Date: Sep 17, 2006
 * Time: 9:00:08 PM
 * To change this template use File | Settings | File Templates.
 */
public class XmlTurnInfoSource extends InfoSource {
    int turnNo;
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

    public int getTurnNo() {
        return turnNo;
    }

    public void setTurnNo(int turnNo) {
        this.turnNo = turnNo;
    }

}
