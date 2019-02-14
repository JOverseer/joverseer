package org.joverseer.orders;

import org.joverseer.game.Turn;
import org.joverseer.support.GameHolder;


public abstract class AbstractTurnPhaseProcessor {
    String name;
    protected GameHolder gameHolder;


    public AbstractTurnPhaseProcessor(String name,GameHolder gameHolder) {
        super();
        this.name = name;
        this.gameHolder = gameHolder;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public abstract void processPhase(Turn t);
}
