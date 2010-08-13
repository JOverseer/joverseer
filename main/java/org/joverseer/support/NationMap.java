package org.joverseer.support;

import org.joverseer.game.Game;
import org.joverseer.metadata.GameMetadata;
import org.joverseer.metadata.domain.Nation;
import org.springframework.richclient.application.Application;


/**
 * Utility class that maps nation numbers to metadata natios
 * 
 * TODO probably it should be removed or moved elsewhere
 * 
 * @author Marios Skounakis
 */
public class NationMap {
    public static Nation getNationFromNo(Integer nationNo) {
        if (nationNo == null) return null;
        Game g = ((GameHolder)Application.instance().getApplicationContext().getBean("gameHolder")).getGame();
        if (Game.isInitialized(g)) {
            GameMetadata gm = g.getMetadata();
            return gm.getNationByNum(nationNo);
        }
        return null;
    }

    public static Nation getNationFromName(String name) {
        if (name == null) return null;
        Game g = ((GameHolder)Application.instance().getApplicationContext().getBean("gameHolder")).getGame();
        if (Game.isInitialized(g)) {
            GameMetadata gm = g.getMetadata();
            return gm.getNationByName(name);
        }
        return null;
    }
}
