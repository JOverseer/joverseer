package org.joverseer.domain;

import org.joverseer.domain.IBelongsToNation;
import org.joverseer.domain.IHasMapLocation;

import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: mskounak
 * Date: 5 Íןו 2006
 * Time: 12:52:55 לל
 * To change this template use File | Settings | File Templates.
 */
public class Order implements IBelongsToNation, IHasMapLocation {
    int nationNo;
    int x;
    int y;

    String characterId;

    int orderNo;
    ArrayList parameters = new ArrayList();

    public int getNationNo() {
        return nationNo;
    }

    public void setNationNo(int nationNo) {
        this.nationNo = nationNo;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public String getCharacterId() {
        return characterId;
    }

    public void setCharacterId(String characterId) {
        this.characterId = characterId;
    }

    public int getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(int orderNo) {
        this.orderNo = orderNo;
    }

    public ArrayList getParameters() {
        return parameters;
    }

    public void setParameters(ArrayList parameters) {
        this.parameters = parameters;
    }

}
