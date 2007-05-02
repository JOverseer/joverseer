package org.joverseer.metadata.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Collection;
import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: mskounak
 * Date: Sep 10, 2006
 * Time: 6:41:13 PM
 *
 * Holds information about a hex
 * - location
 * - terrain
 * - rivers
 * - roads/fords
 * todo rivers and traffic
 */
public class Hex implements Serializable {
    /**
     * 
     */
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

}

