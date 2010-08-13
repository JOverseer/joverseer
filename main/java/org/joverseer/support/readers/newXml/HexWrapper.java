package org.joverseer.support.readers.newXml;

import java.util.ArrayList;

import org.joverseer.game.Game;
import org.joverseer.metadata.domain.Hex;
import org.joverseer.metadata.domain.HexSideElementEnum;
import org.joverseer.metadata.domain.HexSideEnum;

public class HexWrapper {
	int hexID;
	int terrain;
	Integer roads;
	Integer bridges;
	Integer fords;
	Integer minorRivers;
	Integer majorRivers;
	String popCenterName;
	Integer popCenterSize;
	Integer forts;
	Integer ports;
	
	
	
	
	public Integer getForts() {
		return forts;
	}



	public void setForts(Integer forts) {
		this.forts = forts;
	}



	public Integer getPorts() {
		return ports;
	}



	public void setPorts(Integer ports) {
		this.ports = ports;
	}



	public Integer getBridges() {
		return bridges;
	}



	public void setBridges(Integer bridges) {
		this.bridges = bridges;
	}



	public Integer getFords() {
		return fords;
	}



	public void setFords(Integer fords) {
		this.fords = fords;
	}



	public int getHexID() {
		return hexID;
	}



	public void setHexID(int hexID) {
		this.hexID = hexID;
	}



	public Integer getMajorRivers() {
		return majorRivers;
	}



	public void setMajorRivers(Integer majorRivers) {
		this.majorRivers = majorRivers;
	}



	public Integer getMinorRivers() {
		return minorRivers;
	}



	public void setMinorRivers(Integer minorRivers) {
		this.minorRivers = minorRivers;
	}



	public String getPopCenterName() {
		return popCenterName;
	}



	public void setPopCenterName(String popCenterName) {
		this.popCenterName = popCenterName;
	}



	public Integer getPopCenterSize() {
		return popCenterSize;
	}



	public void setPopCenterSize(Integer popCenterSize) {
		this.popCenterSize = popCenterSize;
	}



	public Integer getRoads() {
		return roads;
	}



	public void setRoads(Integer roads) {
		this.roads = roads;
	}



	public int getTerrain() {
		return terrain;
	}



	public void setTerrain(int terrain) {
		this.terrain = terrain;
	}



	public void updateGame(Game game) {
		Hex hex = game.getMetadata().getHex(getHexID());
		hex.clearHexSideElements();
		addElementToSides(hex, HexSideElementEnum.Bridge, getSidesFromInteger(getBridges()));
		addElementToSides(hex, HexSideElementEnum.Ford, getSidesFromInteger(getFords()));
		addElementToSides(hex, HexSideElementEnum.Road, getSidesFromInteger(getRoads()));
		addElementToSides(hex, HexSideElementEnum.MinorRiver, getSidesFromInteger(getMinorRivers()));
		addElementToSides(hex, HexSideElementEnum.MajorRiver, getSidesFromInteger(getMajorRivers()));
	}
	
	protected void addElementToSides(Hex hex, HexSideElementEnum element, ArrayList sides) {
		for (HexSideEnum hse : (ArrayList<HexSideEnum>)sides) {
			hex.addHexSideElement(hse, element);
		}
	}
	
	public ArrayList getSidesFromInteger(int side) {
		ArrayList ret = new ArrayList();
		if (side % 2 == 0) ret.add(HexSideEnum.TopRight);
		if (side % 3 == 0) ret.add(HexSideEnum.Right);
		if (side % 5 == 0) ret.add(HexSideEnum.BottomRight);
		if (side % 7 == 0) ret.add(HexSideEnum.BottomLeft);
		if (side % 11 == 0) ret.add(HexSideEnum.Left);
		if (side % 13 == 0) ret.add(HexSideEnum.TopLeft);
		return ret;
	}
	
	
}
