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
	
	public void setHexNo(int hexNo) {
        setX(hexNo / 100);
        setY(hexNo % 100);
    }
	
	public int getHexNo() {
        return getX() * 100 + getY();
    }
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public PopulationCenterSizeEnum getSize() {
		return size;
	}
	public void setSize(PopulationCenterSizeEnum size) {
		this.size = size;
	}
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	
		
}
