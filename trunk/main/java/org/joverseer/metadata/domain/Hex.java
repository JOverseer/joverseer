package org.joverseer.metadata.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Collection;
import java.io.Serializable;

/**
 * Holds information about a hex
 * - location
 * - terrain
 * - rivers
 * - roads/fords
 * - traffic
 * 
 * All information about the hex sides is stored in a hashmap, key-ed by HexSideEnum items
 * and valued by HexSideElementEnum items.
 * 
 * @author Marios Skounakis
 *
 */
public class Hex implements Serializable {
    private static final long serialVersionUID = 5588445214380293965L;
    int column;
    int row;

    HexTerrainEnum terrain;

    HashMap hexSideElements = new HashMap();

    public Hex() {
        for (HexSideEnum side : HexSideEnum.values()) {
            hexSideElements.put(side, new ArrayList());
        }
    }

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getHexNo() {
        return this.column * 100 + this.row;
    }

    public void setHexNo(int hexNo) {
        column = hexNo / 100;
        row = hexNo % 100;
    }

    public HexTerrainEnum getTerrain() {
        return terrain;
    }

    public void setTerrain(HexTerrainEnum terrain) {
        this.terrain = terrain;
    }

    public Collection getHexSideElements(HexSideEnum side) {
        return (ArrayList)hexSideElements.get(side);
    }

    public void addHexSideElement(HexSideEnum side, HexSideElementEnum element) {
        Collection elements = getHexSideElements(side);
        if (!elements.contains(element)) {
            elements.add(element);
        }
    }
    
    public void removeHexSideElement(HexSideEnum side, HexSideElementEnum element) {
    	Collection elements = getHexSideElements(side);
    	if (elements.contains(element)) {
            elements.remove(element);
        }
    }

    public void clearHexSideElements() {
    	for (HexSideEnum hse : HexSideEnum.values()) {
    		((ArrayList)hexSideElements.get(hse)).clear();
    	}
    }
    
    public ArrayList<HexSideEnum> getHexSidesWithElement(HexSideElementEnum element) {
    	ArrayList<HexSideEnum> ret = new ArrayList<HexSideEnum>();
    	for (HexSideEnum hs : HexSideEnum.values()) {
    		if (getHexSideElements(hs).contains(element)) ret.add(hs);
    	}
    	return ret;
    }
    
    public Hex clone() {
    	Hex h = new Hex();
    	h.setHexNo(getHexNo());
    	h.setTerrain(terrain);
    	for (HexSideEnum side : HexSideEnum.values()) {
    		h.hexSideElements.put(side, ((ArrayList)getHexSideElements(side)).clone());
    	}
    	return h;
    	
    }
}

