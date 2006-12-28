package org.joverseer.game;

import org.joverseer.support.Container;

import java.util.Hashtable;
import java.io.Serializable;


public class Turn implements Serializable {
    int turnNo;

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
    
    
}
