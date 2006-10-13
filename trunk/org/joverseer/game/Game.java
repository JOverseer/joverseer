package org.joverseer.game;

import org.joverseer.metadata.GameMetadata;
import org.joverseer.support.Container;

import java.io.Serializable;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: mskounak
 * Date: Sep 13, 2006
 * Time: 7:43:53 PM
 * To change this template use File | Settings | File Templates.
 */
public class Game implements Serializable {
    GameMetadata metadata;

    Container turns = new Container();

    int maxTurn = -1;

    int currentTurn = -1;

    public GameMetadata getMetadata() {
        return metadata;
    }

    public void setMetadata(GameMetadata metadata) {
        this.metadata = metadata;
    }

    public Container getTurns() {
        return turns;
    }

    public void setTurns(Container turns) {
        this.turns = turns;
    }

    public int getMaxTurn() {
        return maxTurn;
    }

    public void setMaxTurn(int maxTurn) {
        this.maxTurn = maxTurn;
    }

    public Turn getTurn(int turnNo) {
        for (Turn t : (ArrayList<Turn>)getTurns().getItems()) {
            if (t.getTurnNo() == turnNo) {
                return t;
            }
        }
        return null;
    }

    public Turn getTurn() {
        return getTurn(getMaxTurn());
    }

    public void addTurn(Turn turn) throws Exception {
        if (turn.getTurnNo() < getMaxTurn()) {
            throw new Exception("Cannot add past turns to game.");
        }
        turns.addItem(turn);
        setMaxTurn(turn.getTurnNo());
    }

    public int getCurrentTurn() {
        return currentTurn;
    }

    public void setCurrentTurn(int currentTurn) {
        this.currentTurn = currentTurn;
    }

   
}
