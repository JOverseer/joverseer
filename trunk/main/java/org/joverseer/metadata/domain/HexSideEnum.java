package org.joverseer.metadata.domain;

import java.io.Serializable;

import org.joverseer.support.movement.MovementDirection;
import org.joverseer.support.movement.MovementUtils;

/**
 * Enumeration for the hex sides
 * 
 * Values are according to the palantir data files.
 * 
 * @author Marios Skounakis
 *
 */
public enum HexSideEnum implements Serializable {
    TopLeft (6),
    TopRight (1),
    Right (2),
    BottomRight (3),
    BottomLeft (4),
    Left (5);

    private final int side;

    HexSideEnum(int s) {
        this.side = s;
    }

    public int getSide() {
        return this.side;
    }

    public static HexSideEnum fromValue(int i) {
        for (HexSideEnum e : HexSideEnum.values()) {
            if (e.getSide() == i) return e;
        }
        return null;
    }
    
    public int getHexNoAtSide(int currentHexNo) {
    	if (TopLeft.equals(this)) {
    		return MovementUtils.getHexNoAtDir(currentHexNo, MovementDirection.NorthWest);
    	} else if (TopRight.equals(this)) {
    		return MovementUtils.getHexNoAtDir(currentHexNo, MovementDirection.NorthEast);
    	} else if (Right.equals(this)) {
    		return MovementUtils.getHexNoAtDir(currentHexNo, MovementDirection.East);
    	}else if (BottomRight.equals(this)) {
    		return MovementUtils.getHexNoAtDir(currentHexNo, MovementDirection.SouthEast);
    	}else if (BottomLeft.equals(this)) {
    		return MovementUtils.getHexNoAtDir(currentHexNo, MovementDirection.SouthWest);
    	} else if (Left.equals(this)) {
    		return MovementUtils.getHexNoAtDir(currentHexNo, MovementDirection.West);
    	}
    	return -1;
    }
    
    public HexSideEnum getOppositeSide() {
    	if (TopLeft.equals(this)) {
    		return BottomRight;
    	} else if (TopRight.equals(this)) {
    		return BottomLeft;
    	} else if (Right.equals(this)) {
    		return Left;
    	}else if (BottomRight.equals(this)) {
    		return TopLeft;
    	}else if (BottomLeft.equals(this)) {
    		return TopRight;
    	} else if (Left.equals(this)) {
    		return Right;
    	}
    	return null;
    }
}
