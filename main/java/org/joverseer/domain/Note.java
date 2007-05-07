package org.joverseer.domain;

import java.io.Serializable;

import org.joverseer.metadata.domain.Nation;
import org.joverseer.support.NationMap;


public class Note implements IBelongsToNation, IHasMapLocation, Serializable {
    private static final long serialVersionUID = 1459488400804286715L;

    Integer nationNo;
    
    Object target;
    
    String text;
    boolean persistent;
    
    
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


    
    public boolean getPersistent() {
        return persistent;
    }


    
    public void setPersistent(boolean persistent) {
        this.persistent = persistent;
    }
    
    public Nation getNation() {
        return NationMap.getNationFromNo(getNationNo());
    }

    public void setNation(Nation nation) {
        setNationNo(nation.getNumber());
    }

    public int getHexNo() {
        if (getTarget() == null) return 0;
        if (Integer.class.isInstance(getTarget())) {
            return (Integer)getTarget();
        } else if (IHasMapLocation.class.isInstance(getTarget())) {
            return ((IHasMapLocation)getTarget()).getX() * 100 +
            ((IHasMapLocation)getTarget()).getY();
        }
        return 0;
    }
    
    public int getX() {
        return getHexNo() % 100;
    }
    
    public int getY() {
        return getHexNo() / 100;
    }
    
}
