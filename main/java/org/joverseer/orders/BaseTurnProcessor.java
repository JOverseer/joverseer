package org.joverseer.orders;

import java.util.ArrayList;
import java.util.HashMap;

import org.joverseer.game.Turn;


public class BaseTurnProcessor {
    HashMap<Integer, AbstractTurnPhaseProcessor> phases = new HashMap<Integer, AbstractTurnPhaseProcessor>();
    
    public void processTurn(Turn t) {
        ArrayList<Integer> phases = new ArrayList<Integer>();
        phases.addAll(getPhases().keySet());
        for (int phaseNo : phases) {
            AbstractTurnPhaseProcessor processor = getPhases().get(phaseNo);
            processor.processPhase(t);
        }
    }
    
    public HashMap<Integer, AbstractTurnPhaseProcessor> getPhases() {
        return phases;
    }
    
    public void setPhases(HashMap<Integer, AbstractTurnPhaseProcessor> phases) {
        this.phases = phases;
    }
    
    public void addPhase(int phaseNo, AbstractTurnPhaseProcessor phaseProcessor) {
        getPhases().put(phaseNo, phaseProcessor);
    }
    
    
}
