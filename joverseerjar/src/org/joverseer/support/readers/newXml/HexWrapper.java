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
		return this.forts;
	}

	public void setForts(Integer forts) {
		this.forts = forts;
	}

	public Integer getPorts() {
		return this.ports;
	}

	public void setPorts(Integer ports) {
		this.ports = ports;
	}

	public Integer getBridges() {
		return this.bridges;
	}

	public void setBridges(Integer bridges) {
		this.bridges = bridges;
	}

	public Integer getFords() {
		return this.fords;
	}

	public void setFords(Integer fords) {
		this.fords = fords;
	}

	public int getHexID() {
		return this.hexID;
	}

	public void setHexID(int hexID) {
		this.hexID = hexID;
	}

	public Integer getMajorRivers() {
		return this.majorRivers;
	}

	public void setMajorRivers(Integer majorRivers) {
		this.majorRivers = majorRivers;
	}

	public Integer getMinorRivers() {
		return this.minorRivers;
	}

	public void setMinorRivers(Integer minorRivers) {
		this.minorRivers = minorRivers;
	}

	public String getPopCenterName() {
		return this.popCenterName;
	}

	public void setPopCenterName(String popCenterName) {
		this.popCenterName = popCenterName;
	}

	public Integer getPopCenterSize() {
		return this.popCenterSize;
	}

	public void setPopCenterSize(Integer popCenterSize) {
		this.popCenterSize = popCenterSize;
	}

	public Integer getRoads() {
		return this.roads;
	}

	public void setRoads(Integer roads) {
		this.roads = roads;
	}

	public int getTerrain() {
		return this.terrain;
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

	protected void addElementToSides(Hex hex, HexSideElementEnum element, ArrayList<HexSideEnum> sides) {
		for (HexSideEnum hse : sides) {
			hex.addHexSideElement(hse, element);
		}
	}

	public ArrayList<HexSideEnum> getSidesFromInteger(int side) {
		ArrayList<HexSideEnum> ret = new ArrayList<HexSideEnum>();
		if (side % 2 == 0)
			ret.add(HexSideEnum.TopRight);
		if (side % 3 == 0)
			ret.add(HexSideEnum.Right);
		if (side % 5 == 0)
			ret.add(HexSideEnum.BottomRight);
		if (side % 7 == 0)
			ret.add(HexSideEnum.BottomLeft);
		if (side % 11 == 0)
			ret.add(HexSideEnum.Left);
		if (side % 13 == 0)
			ret.add(HexSideEnum.TopLeft);
		return ret;
	}

}
