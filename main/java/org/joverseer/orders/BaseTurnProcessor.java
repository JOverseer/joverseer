package org.joverseer.orders;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

import org.joverseer.game.Turn;


public class BaseTurnProcessor {
    HashMap<String, AbstractTurnPhaseProcessor> phases = new HashMap<String, AbstractTurnPhaseProcessor>();
    
    public Turn copyTurn(Turn t) {
        try {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(os);
            oos.writeObject(t);
            oos.close();
            os.close();
            ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());
            ObjectInputStream ois = new ObjectInputStream(is);
            Turn newTurn = (Turn)ois.readObject();
            ois.close();
            is.close();
            return newTurn;
        }
        catch (Exception exc) {};
        return null;
    }
    
    public void processTurn(Turn t) {
        ArrayList<String> phaseNos = new ArrayList<String>();
        phaseNos.addAll(getPhases().keySet());
        for (String phaseNo : phaseNos) {
            AbstractTurnPhaseProcessor processor = getPhases().get(phaseNo);
            processor.processPhase(t);
        }
    }
    
    public HashMap<String, AbstractTurnPhaseProcessor> getPhases() {
        return phases;
    }
    
    public void setPhases(HashMap<String, AbstractTurnPhaseProcessor> phases) {
        this.phases = phases;
    }
    
    public void addPhase(String phaseNo, AbstractTurnPhaseProcessor phaseProcessor) {
        getPhases().put(phaseNo, phaseProcessor);
    }
    
    
}
