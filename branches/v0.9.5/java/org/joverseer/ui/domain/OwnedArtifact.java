package org.joverseer.ui.domain;

import org.joverseer.domain.IBelongsToNation;
import org.joverseer.domain.IHasMapLocation;


public class OwnedArtifact implements IHasMapLocation, IBelongsToNation {
    int number;
    String name;
    String owner;
    int hexNo;
    Integer nationNo;
    String power1;
    String power2;
    
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

    public String getPower1() {
        return power1;
    }

    
    public void setPower1(String power1) {
        this.power1 = power1;
    }

    
    public String getPower2() {
        return power2;
    }

    
    public void setPower2(String power2) {
        this.power2 = power2;
    }
    
    
    
}
