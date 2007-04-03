package org.joverseer.game;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.zip.GZIPInputStream;

import org.joverseer.metadata.GameMetadata;
import org.joverseer.support.Container;
import org.joverseer.support.GameHolder;
import org.joverseer.ui.LifecycleEventsEnum;
import org.joverseer.ui.support.JOverseerEvent;
import org.springframework.richclient.application.Application;


public class Game implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = -5743076237064103323L;

    GameMetadata metadata;

    Container turns = new Container();

    int maxTurn = -1;

    int currentTurn = -1;
    
    HashMap<String,String> parameters = new HashMap<String, String>();

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
    
    public HashMap<String, String> getParameters() {
        return parameters;
    }
    
    public void setParameters(HashMap<String, String> parameters) {
        this.parameters = parameters;
    }

    public static Game loadGame(File f) throws Exception {
        Game g = null;
        ObjectInputStream in = null;
        try {
            in = new ObjectInputStream(new GZIPInputStream(new FileInputStream(f)));
        }
        catch (Exception exc) {
            // try to read unzipped file
            in = new ObjectInputStream(new FileInputStream(f));
        }
        g = (Game)in.readObject();
        return g;
    }
    
    public String getParameter(String key) {
        return parameters.get(key);
    }
    
    public void setParameter(String key, String value) {
        parameters.put(key, value);
    }
}
