package org.joverseer.domain;

import java.io.Serializable;


public class Note implements IBelongsToNation, IHasMapLocation, Serializable {
	private static final long serialVersionUID = 1459488400804286715L;
	Integer nationNo;
    int hexNo;
    
    Object target;
    
    String text;
    boolean persistent;

    
    public int getHexNo() {
        return hexNo;
    }

    
    public void setHexNo(int hexNo) {
        this.hexNo = hexNo;
    }

    
    public Integer getNationNo() {
        return nationNo;
    }

    
    public void setNationNo(Integer nationNo) {
        this.nationNo = nationNo;
    }

    
    public Object getTarget() {
        return target;
    }

    
    public void setTarget(Object target) {
        this.target = target;
    }

    
    public String getText() {
        return text;
    }

    
    public void setText(String text) {
        this.text = text;
    }


    public int getX() {
        return getHexNo() / 100;
    }


    public int getY() {
        return getHexNo() % 100;
    }


    
    public boolean getPersistent() {
        return persistent;
    }


    
    public void setPersistent(boolean persistent) {
        this.persistent = persistent;
    }
    
    
}
