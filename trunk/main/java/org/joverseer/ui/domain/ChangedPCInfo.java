package org.joverseer.ui.domain;

import org.joverseer.domain.IBelongsToNation;
import org.joverseer.domain.IHasMapLocation;
import org.joverseer.domain.PopulationCenterSizeEnum;

public class ChangedPCInfo implements IHasMapLocation, IBelongsToNation {
	Integer nationNo;
	int x;
	int y;
	String name;
	PopulationCenterSizeEnum size;
	String reason;
	
	@Override
	public Integer getNationNo() {
		return this.nationNo;
	}
	@Override
	public void setNationNo(Integer nationNo) {
		this.nationNo = nationNo;
	}
	@Override
	public int getX() {
		return this.x;
	}
	public void setX(int x) {
		this.x = x;
	}
	@Override
	public int getY() {
		return this.y;
	}
	public void setY(int y) {
		this.y = y;
	}
	
	public void setHexNo(int hexNo) {
        setX(hexNo / 100);
        setY(hexNo % 100);
    }
	
	public int getHexNo() {
        return getX() * 100 + getY();
    }
	public String getName() {
		return this.name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public PopulationCenterSizeEnum getSize() {
		return this.size;
	}
	public void setSize(PopulationCenterSizeEnum size) {
		this.size = size;
	}
	public String getReason() {
		return this.reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	
		
}
