package org.joverseer.ui.domain;

import org.joverseer.domain.IBelongsToNation;
import org.joverseer.domain.IHasMapLocation;


public class OwnedArtifact implements IHasMapLocation, IBelongsToNation{
    int number;
    String name;
    String owner;
    int hexNo;
    Integer nationNo;
    
    public int getX() {
        return hexNo / 100;
    }
    
    public int getY() {
        return hexNo % 100;
    }

    public int getHexNo() {
        return hexNo;
    }
    
    public void setHexNo(int hexNo) {
        this.hexNo = hexNo;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public Integer getNationNo() {
        return nationNo;
    }
    
    public void setNationNo(Integer nationNo) {
        this.nationNo = nationNo;
    }
    
    public int getNumber() {
        return number;
    }
    
    public void setNumber(int number) {
        this.number = number;
    }
    
    public String getOwner() {
        return owner;
    }
    
    public void setOwner(String owner) {
        this.owner = owner;
    }
    
    
}
