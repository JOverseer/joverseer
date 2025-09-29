package org.joverseer.metadata.domain;

import java.awt.Rectangle;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;



/**
 * Holds the hex ranges that define the nation's map (as reported in the turn xml and pdf
 * files)
 * 
 * @author Marios Skounakis
 *
 */
public class NationMapRange implements Serializable {
    private static final long serialVersionUID = 7251072341355206761L;
    int nationNo;
    int tlX;
    int tlY;
    int brX;
    int brY;
    // A list of all the points, in sets of 4 that each make up a rectangle
    List<Integer> points=new ArrayList<Integer>();

    public int getBrX() {
        return this.brX;
    }

    public void setBrX(int brX) {
        this.brX = brX;
    }

    public int getBrY() {
        return this.brY;
    }

    public void setBrY(int brY) {
        this.brY = brY;
    }

    public int getTlX() {
        return this.tlX;
    }

    public void setTlX(int tlX) {
        this.tlX = tlX;
    }

    public int getTlY() {
        return this.tlY;
    }

    public void setTlY(int tlY) {
        this.tlY = tlY;
    }
    
    public Rectangle getRectangle() {
        return new Rectangle(getTlX(), getTlY(), getBrX() - getTlX() + 1, getBrY() - getTlY() + 1);
    }

    public int getNationNo() {
        return this.nationNo;
    }

    public void setNationNo(int nationNo) {
        this.nationNo = nationNo;
    }

	public boolean containsHex(Hex hex) {
		// For non-FA games, simply check if the hex is in the rectangle
		if (this.getRectangle().contains(hex.getColumn(), hex.getRow())) {
            if (this.getRectangle().getX() + this.getRectangle().getWidth() == hex.getColumn() + 1) {
            	if (hex.getRow() % 2 == 1) {
            		return true;
            	} else {
            		return this.pointsContainHex(hex);
            	}
            }
            return true;
        }
		
		return this.pointsContainHex(hex);
		
	}
	
	private boolean pointsContainHex(Hex hex) {
		// For FA games run through all the additional hexes in the list
		if (this.points!=null) {
			for (int i=0; i<this.points.size();i=i+2) {
				if (this.points.get(i)==hex.getColumn()) {
					if (this.points.get(i+1)==hex.getRow()) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public void setPoints(List<Integer> points) {
		this.points = points;
		
	}
	
	public List<Integer> getPoints() {
		return this.points;
	}

}

