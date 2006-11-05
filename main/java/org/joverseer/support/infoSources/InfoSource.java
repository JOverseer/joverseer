package org.joverseer.support.infoSources;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: mskounak
 * Date: Sep 17, 2006
 * Time: 8:58:11 PM
 * To change this template use File | Settings | File Templates.
 */
public class InfoSource implements Serializable {
    int turnNo;

    public int getTurnNo() {
        return turnNo;
    }

    public void setTurnNo(int turnNo) {
        this.turnNo = turnNo;
    }
}
