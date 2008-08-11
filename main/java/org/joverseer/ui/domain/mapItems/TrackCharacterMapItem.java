package org.joverseer.ui.domain.mapItems;

import java.util.ArrayList;

public class TrackCharacterMapItem extends AbstractMapItem {
	private static final long serialVersionUID = -8481077157847653822L;
	ArrayList<Integer> hexes = new ArrayList<Integer>();
	ArrayList<Integer> turns = new ArrayList<Integer>();
	
	String description;
    
    public void setDescription(String descr) {
        this.description = descr;
    }
    
    public String getDescription() {
        return description;
    }

	public ArrayList<Integer> getHexes() {
		return hexes;
	}

	public void setHexes(ArrayList<Integer> hexes) {
		this.hexes = hexes;
	}

	public ArrayList<Integer> getTurns() {
		return turns;
	}

	public void setTurns(ArrayList<Integer> turns) {
		this.turns = turns;
	}

    public void addPoint(int hex, int turn) {
    	hexes.add(hex);
    	turns.add(turn);
    }
}