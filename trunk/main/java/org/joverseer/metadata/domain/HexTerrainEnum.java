package org.joverseer.metadata.domain;

import java.io.Serializable;


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
}
