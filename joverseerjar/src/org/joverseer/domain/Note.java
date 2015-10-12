package org.joverseer.domain;

import java.io.Serializable;

import org.joverseer.metadata.domain.Nation;
import org.joverseer.support.NationMap;

/**
 * Stores a note made by the player
 * 
 * Notes have
 * - a nation
 * - a location
 * - a target, which can be generally a character or a hex
 * - a text
 * - a set of tags (comma separated list)
 * - a unique id
 * - a flag whether they are persistent
 * 
 * @author Marios Skounakis
 */
public class Note implements IBelongsToNation, IHasMapLocation, Serializable {
    private static final long serialVersionUID = 1459488400804286715L;

    Integer nationNo;
    
    Object target;
    
    String text;
    boolean persistent;
    String tags;
    long id;
    
    @Override
	public Integer getNationNo() {
        if (this.nationNo == null) {
            if (Integer.class.isInstance(getTarget())) {
                return 0;
            } else if (IBelongsToNation.class.isInstance(getTarget())) {
                return ((IBelongsToNation)getTarget()).getNationNo();
            }
            return 0;
        }
        return this.nationNo;
    }

    @Override
	public void setNationNo(Integer n) {
        this.nationNo = n;
    };
    
    public Object getTarget() {
        return this.target;
    }

    
    public void setTarget(Object target) {
        this.target = target;
    }

    
    public String getText() {
        return this.text;
    }

    
    public void setText(String text) {
        this.text = text;
    }


    
    public boolean getPersistent() {
        return this.persistent;
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
    
    @Override
	public int getX() {
        return getHexNo() / 100;
    }
    
    @Override
	public int getY() {
        return getHexNo() % 100;
    }
    
    public String getTargetDescription() {
        if (getTarget() == null) return "";
        if (Integer.class.isInstance(getTarget())) {
            return getTarget().toString();
        } else if (Character.class.isInstance(getTarget())) {
            return ((Character)getTarget()).getName();
        } else if (PopulationCenter.class.isInstance(getTarget())) {
            return ((PopulationCenter)getTarget()).getName();
        } else if (Army.class.isInstance(getTarget())) {
            return ((Army)getTarget()).getCommanderName() + "'s army";
        }
        return "";
    }

    
    public String getTags() {
        return this.tags;
    }

    
    public void setTags(String tags) {
        this.tags = tags;
    }

    
    public long getId() {
        return this.id;
    }

    
    public void setId(long id) {
        this.id = id;
    }
    
    
    
}
