package org.joverseer.metadata;

import org.joverseer.metadata.domain.Nation;
import org.joverseer.support.Container;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.Collection;
import java.io.Serializable;
import java.io.IOException;

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
public class GameMetadata implements Serializable {
    GameTypeEnum gameType;
    int gameNo;

    ArrayList hexes = new ArrayList();
    ArrayList nations = new ArrayList();
    Container artifacts = new Container();
    Container orders = new Container();

    Container populationCenters = new Container();

    ArrayList readers = new ArrayList();

    String basePath;

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
        this.hexes.addAll(hexes);
    }

    public ArrayList getNations() {
        return nations;
    }

    public void setNations(Collection nations) {
        this.nations.addAll(nations);
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

    private void writeObject(java.io.ObjectOutputStream out) throws IOException {
        out.writeObject(getArtifacts());
        out.writeObject(getOrders());
        out.writeObject(getHexes());
        out.writeObject(getNations());
        out.writeObject(getGameType());
        out.writeObject(getGameNo());
    }

    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
        artifacts = (Container)in.readObject();
        orders = (Container)in.readObject();
        hexes = (ArrayList)in.readObject();
        nations = (ArrayList)in.readObject();
        setGameType((GameTypeEnum)in.readObject());
        setGameNo((Integer)in.readObject());
    }

    public Nation getNationByNum(int number) {
        for (Nation n : (ArrayList<Nation>)getNations()) {
            if (n.getNumber() == number) {
                return n;
            }
        }
        return null;
    }

    public Container getArtifacts() {
        return artifacts;
    }

    public void setArtifacts(Container artifacts) {
        this.artifacts = artifacts;
    }

    public Container getPopulationCenters() {
        return populationCenters;
    }

    public void setPopulationCenters(Container populationCenters) {
        this.populationCenters = populationCenters;
    }

    public Container getOrders() {
        return orders;
    }

    public void setOrders(Container orders) {
        this.orders = orders;
    }

    public String getBasePath() {
        return basePath;
    }

    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }
}
