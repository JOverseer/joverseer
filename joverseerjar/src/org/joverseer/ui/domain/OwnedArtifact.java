package org.joverseer.ui.domain;

import org.joverseer.domain.IBelongsToNation;
import org.joverseer.domain.IHasMapLocation;

/**
 * Wraps artifact info for the Owned Artifacts list view
 * 
 * @author Marios Skounakis
 */
public class OwnedArtifact implements IHasMapLocation, IBelongsToNation {
    int number;
    String name;
    String owner;
    int hexNo;
    Integer nationNo;
    String power1;
    String power2;
    
    @Override
	public int getX() {
        return this.hexNo / 100;
    }
    
    @Override
	public int getY() {
        return this.hexNo % 100;
    }

    public int getHexNo() {
        return this.hexNo;
    }
    
    public void setHexNo(int hexNo) {
        this.hexNo = hexNo;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    @Override
	public Integer getNationNo() {
        return this.nationNo;
    }
    
    @Override
	public void setNationNo(Integer nationNo) {
        this.nationNo = nationNo;
    }
    
    public int getNumber() {
        return this.number;
    }
    
    public void setNumber(int number) {
        this.number = number;
    }
    
    public String getOwner() {
        return this.owner;
    }
    
    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getPower1() {
        return this.power1;
    }

    
    public void setPower1(String power1) {
        this.power1 = power1;
    }

    
    public String getPower2() {
        return this.power2;
    }

    
    public void setPower2(String power2) {
        this.power2 = power2;
    }
    
    
    
}
