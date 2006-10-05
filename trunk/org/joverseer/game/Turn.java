package org.joverseer.game;

import org.joverseer.support.Container;

import java.util.Hashtable;

/**
 * Created by IntelliJ IDEA.
 * User: mskounak
 * Date: Sep 13, 2006
 * Time: 7:44:43 PM
 * To change this template use File | Settings | File Templates.
 */
public class Turn {
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
        return getContainers().get(turnElement);
    }
}
