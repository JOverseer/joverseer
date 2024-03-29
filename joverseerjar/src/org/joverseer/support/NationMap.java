package org.joverseer.support;

import org.joverseer.JOApplication;
import org.joverseer.game.Game;
import org.joverseer.metadata.GameMetadata;
import org.joverseer.metadata.domain.Nation;


/**
 * Utility class that maps nation numbers to metadata nations
 * 
 * TODO probably it should be removed or moved elsewhere - an interface on gm but expressed as a dependency for clients.
 * eg NationMapper INationMap and get it auto-wired from gm
 * 
 * @author Marios Skounakis
 */
public class NationMap {
    public static Nation getNationFromNo(Integer nationNo) {
        if (nationNo == null) return null;
        Game g = JOApplication.getGame();
        if (Game.isInitialized(g)) {
            GameMetadata gm = g.getMetadata();
            return gm.getNationByNum(nationNo);
        }
        return null;
    }

    public static Nation getNationFromName(String name) {
        if (name == null) return null;
        Game g = JOApplication.getGame();
        if (Game.isInitialized(g)) {
            GameMetadata gm = g.getMetadata();
            return gm.getNationByName(name);
        }
        return null;
    }
}
