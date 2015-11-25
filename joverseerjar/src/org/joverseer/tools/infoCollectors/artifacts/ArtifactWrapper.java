package org.joverseer.tools.infoCollectors.artifacts;

import org.joverseer.domain.IBelongsToNation;
import org.joverseer.domain.IHasMapLocation;
import org.joverseer.domain.IHasTurnNumber;
import org.joverseer.support.infoSources.InfoSource;

/**
 * Wraps information about an artifact. Used by the artifact info collector.
 * 
 * 
 * @author Marios Skounakis
 *
 */
public class ArtifactWrapper implements IHasMapLocation, IBelongsToNation, IHasTurnNumber {
    int hexNo;
    Integer nationNo;
    int turnNo;
    
    String name;
    int number;
    String owner;
    String power1;
    String power2;
    InfoSource infoSource;
    String alignment;
    
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
    
    @Override
	public int getTurnNo() {
        return this.turnNo;
    }
    
    public void setTurnNo(int turnNo) {
        this.turnNo = turnNo;
    }

    @Override
	public int getX() {
        return getHexNo() / 100;
    }

    @Override
	public int getY() {
        return getHexNo() % 100;
    }

    
    public InfoSource getInfoSource() {
        return this.infoSource;
    }

    
    public void setInfoSource(InfoSource infoSource) {
        this.infoSource = infoSource;
    }

    public String getAlignment() {
    	return this.alignment;
    }
    
    public void setAlignment(String alignment) {
    	this.alignment = alignment;
    }

    }
