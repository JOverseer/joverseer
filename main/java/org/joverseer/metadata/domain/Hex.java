package org.joverseer.metadata.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Holds information about a hex - location - terrain - rivers - roads/fords -
 * traffic
 * 
 * All information about the hex sides is stored in a hashmap, key-ed by
 * HexSideEnum items and valued by HexSideElementEnum items.
 * 
 * @author Marios Skounakis
 * 
 */
public class Hex implements Serializable {
	private static final long serialVersionUID = 5588445214380293965L;
	int column;
	int row;

	HexTerrainEnum terrain;

	HashMap<HexSideEnum, ArrayList<HexSideElementEnum>> hexSideElements = new HashMap<HexSideEnum, ArrayList<HexSideElementEnum>>();

	public Hex() {
		for (HexSideEnum side : HexSideEnum.values()) {
			this.hexSideElements.put(side, new ArrayList<HexSideElementEnum>());
		}
	}

	public int getColumn() {
		return this.column;
	}

	public void setColumn(int column) {
		this.column = column;
	}

	public int getRow() {
		return this.row;
	}

	public void setRow(int row) {
		this.row = row;
	}

	public int getHexNo() {
		return this.column * 100 + this.row;
	}

	public void setHexNo(int hexNo) {
		this.column = hexNo / 100;
		this.row = hexNo % 100;
	}

	public HexTerrainEnum getTerrain() {
		return this.terrain;
	}

	public void setTerrain(HexTerrainEnum terrain) {
		this.terrain = terrain;
	}

	public ArrayList<HexSideElementEnum> getHexSideElements(HexSideEnum side) {
		return this.hexSideElements.get(side);
	}

	public void addHexSideElement(HexSideEnum side, HexSideElementEnum element) {
		ArrayList<HexSideElementEnum> elements = getHexSideElements(side);
		if (!elements.contains(element)) {
			elements.add(element);
		}
	}

	public void removeHexSideElement(HexSideEnum side, HexSideElementEnum element) {
		ArrayList<HexSideElementEnum> elements = getHexSideElements(side);
		if (elements.contains(element)) {
			elements.remove(element);
		}
	}

	public void clearHexSideElements() {
		for (HexSideEnum hse : HexSideEnum.values()) {
			this.hexSideElements.get(hse).clear();
		}
	}

	public ArrayList<HexSideEnum> getHexSidesWithElement(HexSideElementEnum element) {
		ArrayList<HexSideEnum> ret = new ArrayList<HexSideEnum>();
		for (HexSideEnum hs : HexSideEnum.values()) {
			if (getHexSideElements(hs).contains(element))
				ret.add(hs);
		}
		return ret;
	}

	// only used by orderchecker integration
	public HashMap<HexSideEnum, ArrayList<HexSideElementEnum>> getSideElements() {
		return this.hexSideElements;
	}
	@SuppressWarnings("unchecked")
	@Override
	public Hex clone() {
		Hex h = new Hex();
		h.setHexNo(getHexNo());
		h.setTerrain(this.terrain);
		for (HexSideEnum side : HexSideEnum.values()) {
			h.hexSideElements.put(side, (ArrayList<HexSideElementEnum>) getHexSideElements(side).clone());
		}
		return h;

	}
}
