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
    
    @Override
	public String getDescription() {
        return this.description;
    }

	public ArrayList<Integer> getHexes() {
		return this.hexes;
	}

	public void setHexes(ArrayList<Integer> hexes) {
		this.hexes = hexes;
	}

	public ArrayList<Integer> getTurns() {
		return this.turns;
	}

	public void setTurns(ArrayList<Integer> turns) {
		this.turns = turns;
	}

    public void addPoint(int hex, int turn) {
    	this.hexes.add(hex);
    	this.turns.add(turn);
    }

	@Override
	public boolean isEquivalent(AbstractMapItem mi) {
		return (mi instanceof TrackCharacterMapItem)
				&& (this.description == ((TrackCharacterMapItem)mi).description) 
				&& (this.hexes == ((TrackCharacterMapItem)mi).hexes) 
				&& (this.turns == ((TrackCharacterMapItem)mi).turns); 
	}
}
