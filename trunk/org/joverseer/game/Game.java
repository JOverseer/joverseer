package org.joverseer.game;

import org.joverseer.metadata.GameMetadata;
import org.joverseer.support.Container;

/**
 * Created by IntelliJ IDEA.
 * User: mskounak
 * Date: Sep 13, 2006
 * Time: 7:43:53 PM
 * To change this template use File | Settings | File Templates.
 */
public class Game {
    GameMetadata metadata;

    Container turns = new Container();

    int maxTurn = -1;

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
        if (turns.size() > turnNo) {
            return (Turn)turns.findFirstByProperty("turnNo", turnNo);
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
}
