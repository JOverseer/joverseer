package org.joverseer.domain;

import org.joverseer.domain.IBelongsToNation;
import org.joverseer.domain.IHasMapLocation;
import org.joverseer.metadata.GameMetadata;
import org.joverseer.metadata.orders.OrderMetadata;
import org.joverseer.support.GameHolder;
import org.joverseer.support.Container;
import org.springframework.richclient.application.Application;

import java.util.ArrayList;
import java.io.Serializable;


public class Order implements IBelongsToNation, IHasMapLocation, Serializable {
    int nationNo;

    int orderNo = -1;
    String parameters = "";

    Character character;

    public Order(Character c) {
        character = c;
    }

    public int getNationNo() {
        return nationNo;
    }

    public void setNationNo(int nationNo) {
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
        int i = noAndDescr.indexOf(' ');
        String no = noAndDescr.substring(0, i);
        setOrderNo(Integer.parseInt(no));
        // todo handle exceptions
    }

    public String getNoAndCode() {
        if (getOrderNo() <= 0) return "N/A";
        OrderMetadata om = getMetadata();
        if (om == null) return "N/A";
        return om.getNumber() + " " + om.getCode();
    }

    public OrderMetadata getMetadata() {
        GameMetadata gm = ((GameHolder) Application.instance().getApplicationContext().getBean("gameHolder")).getGame().getMetadata();
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
}
