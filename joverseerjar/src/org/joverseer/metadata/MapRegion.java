package org.joverseer.metadata;

import java.io.Serializable;

public class MapRegion implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int regionNo;
	private String regionName;
	private String nationInRegion;
	
	public String getRegionName() {
		return this.regionName;
	}
	public void setRegionName(String regionName) {
		this.regionName = regionName;
	}
	public int getRegionNo() {
		return this.regionNo;
	}
	public void setRegionNo(int regionNo) {
		this.regionNo = regionNo;
	}
	public String getNationInRegion() {
		return this.nationInRegion;
	}
	public void setNationInRegion(String nationInRegion) {
		this.nationInRegion = nationInRegion;
	}
	
	

}
