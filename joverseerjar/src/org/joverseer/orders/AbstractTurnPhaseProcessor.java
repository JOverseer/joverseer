package org.joverseer.orders;

import org.joverseer.game.Turn;


public abstract class AbstractTurnPhaseProcessor {
    String name;
    
    
    
    public AbstractTurnPhaseProcessor(String name) {
        super();
        this.name = name;
    }



    public String getName() {
        return this.name;
    }


    
    public void setName(String name) {
        this.name = name;
    }


    public abstract void processPhase(Turn t);
}
