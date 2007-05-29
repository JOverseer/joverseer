package org.joverseer.support.infoSources;

import java.io.Serializable;

/**
 * Basic class for Info Source
 * 
 * An Info Source represents a source of information for a piece of data. All Info Sources have
 * a turn number (the turn the information was retrieved).
 * 
 * @author Marios Skounakis
 *
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
