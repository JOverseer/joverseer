package org.joverseer.game;

import java.io.Serializable;
import java.util.ArrayList;

import org.joverseer.metadata.GameMetadata;
import org.joverseer.support.Container;


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
        if (getCurrentTurn() == -1) {
            return getTurn(getMaxTurn());
        }
        return getTurn(getCurrentTurn());
    }

    public void addTurn(Turn turn) throws Exception {
        if (turn.getTurnNo() < getMaxTurn()) {
            throw new Exception("Cannot add past turns to game.");
        }
        turns.addItem(turn);
        setMaxTurn(turn.getTurnNo());
        setCurrentTurn(turn.getTurnNo());
    }

    public int getCurrentTurn() {
        return currentTurn;
    }

    public void setCurrentTurn(int currentTurn) {
        this.currentTurn = currentTurn;
    }

    public static boolean isInitialized(Game g) {
        return (g != null && g.getTurn() != null);
    }

   
}
