package org.joverseer.metadata;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 * User: mskounak
 * Date: Sep 10, 2006
 * Time: 10:00:02 PM
 *
 * Holds metadata about the game such as
 * 1. the game type and other game instance stuff
 * 2. information that depends on the game type, such as the hexes, the artifacts, etc 
 */
public class GameMetadata {
    GameTypeEnum gameType;
    int gameNo;

    Collection hexes = new ArrayList();

    ArrayList readers = new ArrayList();

    public GameTypeEnum getGameType() {
        return gameType;
    }

    public void setGameType(GameTypeEnum gameType) {
        this.gameType = gameType;
    }

    public Collection getHexes() {
        return hexes;
    }

    public void setHexes(Collection hexes) {
        this.hexes = hexes;
    }

    public void load() {
        for (MetadataReader r : (Collection<MetadataReader>)getReaders()) {
            r.load(this);
        }
    }

    public ArrayList getReaders() {
        return readers;
    }

    public void setReaders(ArrayList readers) {
        this.readers = readers;
    }

    public int getGameNo() {
        return gameNo;
    }

    public void setGameNo(int gameNo) {
        this.gameNo = gameNo;
    }
}
