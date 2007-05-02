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

    private static final long serialVersionUID = 1643607461991378403L;
    public static String NA = " N/A";
    public static String DELIM = "#";
    Integer nationNo;

    int orderNo = -1;
    String parameters = "";
    
    String notes;

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
        if (i == -1) {
            i = 3;
        }
        String no = noAndDescr.substring(0, i);
        try {
            setOrderNo(Integer.parseInt(no));
        }
        catch (Exception exc) {
            clear();
        }
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
        // simple placeholder so that form code can work with Order
    }

    public boolean isBlank() {
        return orderNo <= 0;
    }

    public String getParameter(int i) {
        String[] params = getParameters().split(DELIM);
        if (params.length > i) {
            return params[i];
        }
        return null;
    }
    
    
    
    
    public String getNotes() {
        return notes;
    }

    
    public void setNotes(String notes) {
        this.notes = notes;
    }

    public void clear() {
        orderNo = -1;
        parameters = "";
        notes = "";
    }
    
    public static String getParametersAsString(String params) {
        return params.replace(DELIM, " ");
    }
    
    public void setParameters(String[] params) {
        String p = "";
        for (String param : params) {
            p += (p.equals("") ? "" : DELIM) + param;
        }
        setParameters(p);
    }
    
    public void setParameter(int idx, String param) {
        String[] params = parameters.split(Order.DELIM);
        if (idx < params.length) {
            params[idx] = param;
            setParameters(paramStringFromArray(params));
        } else {
            String[] ps = new String[idx + 1];
            for (int i=0; i<params.length; i++) {
                ps[i] = params[i];
            }
            for (int i=params.length; i<idx; i++) {
                ps[i] = "-";
            }
            ps[idx] = param;
            setParameters(paramStringFromArray(ps));
        }
    }
    
    public static String paramStringFromArray(String[] ps) {
        String p = "";
        for (String pm : ps) {
            p += (p.equals("") ? "" : Order.DELIM) + pm;
        }
        return p;
    }
    
    public int getLastParamIndex() {
        int i = 0;
        for (int j=0; j<16; j++) {
            if (getParameter(j) != null) {
                i = j;
            }
        }
        return i;
    }
}
