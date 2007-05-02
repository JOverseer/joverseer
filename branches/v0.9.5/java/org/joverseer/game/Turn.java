package org.joverseer.game;

import org.joverseer.domain.SeasonEnum;
import org.joverseer.support.Container;

import java.util.Date;
import java.util.Hashtable;
import java.io.Serializable;


public class Turn implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 8759609718974408867L;
    int turnNo;
    Date turnDate;
    SeasonEnum season;
    
    Hashtable<TurnElementsEnum, Container> containers = new Hashtable<TurnElementsEnum, Container>();

    public int getTurnNo() {
        return turnNo;
    }

    public void setTurnNo(int turnNo) {
        this.turnNo = turnNo;
    }

    public Hashtable<TurnElementsEnum, Container> getContainers() {
        return containers;
    }

    public void setContainers(Hashtable<TurnElementsEnum, Container> containers) {
        this.containers = containers;
    }

    public Container getContainer(TurnElementsEnum turnElement) {
        Container c = getContainers().get(turnElement);
        if (c == null) {
            c = new Container();
            getContainers().put(turnElement, c);
        }
        return c;
    }

    
    public SeasonEnum getSeason() {
        return season;
    }

    
    public void setSeason(SeasonEnum season) {
        this.season = season;
    }

    
    public Date getTurnDate() {
        return turnDate;
    }

    
    public void setTurnDate(Date turnDate) {
        this.turnDate = turnDate;
    }
    
    
}
