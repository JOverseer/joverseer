package org.joverseer.tools;

import java.util.ArrayList;

import org.joverseer.game.Game;
import org.joverseer.game.Turn;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.support.GameHolder;


public class ItemFinder {
    
    public TurnElementsEnum getSearchType(String searchString) {
        if (searchString.indexOf(":") < 0) return null;
        String prefix = searchString.substring(0, searchString.indexOf(":"));
        if (prefix.equals("char")) {
            return TurnElementsEnum.Character;
        } else if (prefix.equals("pc")) {
            return TurnElementsEnum.PopulationCenter;
        } else if (prefix.equals("msg")) {
            return TurnElementsEnum.NationMessage;
        } else if (prefix.equals("enc")) {
            return TurnElementsEnum.Encounter;
        } else if (prefix.equals("arti")) {
            return TurnElementsEnum.Artifact;
        } else if (prefix.equals("army")) {
            return TurnElementsEnum.Army;
        }
        return null;
    }
    
    public ArrayList<Object> find(String searchString) {
        ArrayList<Object> ret = new ArrayList<Object>();
        Game g = GameHolder.instance().getGame();
        if (!Game.isInitialized(g)) return ret;
        Turn t = g.getTurn();
        // search characters
        
        // search pcs
        // search rumors
        // search armies
        // search encounters/combats
        return ret;
    }
}
