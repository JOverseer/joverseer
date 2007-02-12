package org.joverseer.domain;

import org.joverseer.domain.IBelongsToNation;
import org.joverseer.domain.IHasMapLocation;
import org.joverseer.game.Game;
import org.joverseer.metadata.GameMetadata;
import org.joverseer.metadata.orders.OrderMetadata;
import org.joverseer.support.GameHolder;
import org.joverseer.support.Container;
import org.springframework.richclient.application.Application;

import java.util.ArrayList;
import java.io.Serializable;


public class Order implements IBelongsToNation, IHasMapLocation, Serializable {
    public static String NA = "N/A";
    Integer nationNo;

    int orderNo = -1;
    String parameters = "";

    Character character;

    public Order(Character c) {
        character = c;
    }

    public Integer getNationNo() {
        return nationNo;
    }

    public void setNationNo(Integer nationNo) {
        this.nationNo = nationNo;
    }

    public int getX() {
        return getCharacter().getX();
    }

    public void setX(int x) {
    }

    public int getY() {
        return getCharacter().getY();
    }

    public void setY(int y) {
    }

    public int getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(int orderNo) {
        this.orderNo = orderNo;
    }

    public String getParameters() {
        return parameters;
    }

    public void setParameters(String parameters) {
        this.parameters = parameters;
    }

    public Character getCharacter() {
        return character;
    }

    public void setCharacter(Character character) {
        this.character = character;
    }

    public void setNoAndCode(String noAndDescr) {
        if (noAndDescr.equals(NA)) {
            setOrderNo(-1);
            return;
        }
        int i = noAndDescr.indexOf(' ');
        String no = noAndDescr.substring(0, i);
        setOrderNo(Integer.parseInt(no));
        // todo handle exceptions
    }

    public String getNoAndCode() {
        if (getOrderNo() <= 0) return NA;
        OrderMetadata om = getMetadata();
        if (om == null) return NA;
        return om.getNumber() + " " + om.getCode();
    }

    public OrderMetadata getMetadata() {
        Game g = ((GameHolder) Application.instance().getApplicationContext().getBean("gameHolder")).getGame();
        if (g == null) return null;
        GameMetadata gm = g.getMetadata();
        if (gm == null) return null;
        OrderMetadata om = (OrderMetadata)gm.getOrders().findFirstByProperty("number", getOrderNo());
        return om;
    }

    public String getMetadataDescription() {
        OrderMetadata om = getMetadata();
        if (om == null) return "";
        return om.getName() + " , " + om.getDifficulty() + ", " + om.getRequirement();
    }

    public void setMetadataDescription(String value) {
        // do nothing
        // simple placeholder so that it form code can work with Order
    }

    public boolean isBlank() {
        return orderNo <= 0;
    }

    public String getParameter(int i) {
        String[] params = getParameters().split(" ");
        if (params.length > i) {
            return params[i];
        }
        return null;
    }
    
    public void clear() {
        orderNo = -1;
        parameters = "";
        
    }
}
