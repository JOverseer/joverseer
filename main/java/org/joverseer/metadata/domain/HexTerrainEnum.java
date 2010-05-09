package org.joverseer.metadata.domain;

import java.io.Serializable;
import java.lang.reflect.Array;

import org.joverseer.ui.support.UIUtils;

/**
 * Enumeration for hex terains
 * 
 * Values are according to the palantir data files. 
 * 
 * @author Marios Skounakis
 *
 */
public enum HexTerrainEnum implements Serializable {
    plains(1),
    shore(2),
    forest (3),
    swamp (4),
    hillsNrough(5),
    mountains (6),
    desert (7),
    sea (8),
    ocean (9);

    int terrain;



    private HexTerrainEnum(int t) {
        terrain = t;
    }

    public static HexTerrainEnum fromValue(int i) {
        for (HexTerrainEnum h : HexTerrainEnum.values()) {
            if (i == h.getTerrain()) {
                return h;
            }
        }
        return null;
    }

    public int getTerrain() {
        return terrain;
    }
    
    public static HexTerrainEnum[] landValues() {
        return new HexTerrainEnum[]{plains, shore, forest, swamp, hillsNrough, mountains, desert};
    }
    
    public static HexTerrainEnum[] openSeaValues() {
        return new HexTerrainEnum[]{ocean, sea};
    }
    
    public boolean isLand() {
    	for (HexTerrainEnum hte : landValues()) {
    		if (hte.equals(this)) return true;
    	}
    	return false;
    }
    
    public boolean isOpenSea() {
    	for (HexTerrainEnum hte : openSeaValues()) {
    		if (hte.equals(this)) return true;
    	}
    	return false;
    }
    
    public String getRenderString() {
 	   return UIUtils.enumToString(this);
    }
}
