package org.joverseer.domain;

import java.io.Serializable;


public class NationMessage implements Serializable, IBelongsToNation, IHasMapLocation {
    /**
     * 
     */
    private static final long serialVersionUID = -5607141998688317604L;
    int x = -1;
    int y = -1;
    Integer nationNo;

    int x2;
    int y2;

    String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getNationNo() {
        return nationNo;
    }

    public void setNationNo(Integer nationNo) {
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

    public int getX2() {
        return x2;
    }

    public void setX2(int x2) {
        this.x2 = x2;
    }

    public int getY2() {
        return y2;
    }

    public void setY2(int y2) {
        this.y2 = y2;
    }
}
