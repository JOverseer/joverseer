package org.joverseer.support.infoSources;

import java.io.Serializable;


public class InfoSource implements Serializable {
    int turnNo;

    public int getTurnNo() {
        return turnNo;
    }

    public void setTurnNo(int turnNo) {
        this.turnNo = turnNo;
    }
}
