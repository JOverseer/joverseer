package org.joverseer.support.readers.newXml;

import java.util.ArrayList;

import org.joverseer.game.Game;

public class ReconWrapper {
	ArrayList<HexWrapper> hexWrappers = new ArrayList<HexWrapper>();

	public ArrayList<HexWrapper> getHexWrappers() {
		return this.hexWrappers;
	}

	public void setHexWrappers(ArrayList<HexWrapper> hexWrappers) {
		this.hexWrappers = hexWrappers;
	}
	
	public void addHexWrapper(HexWrapper rhw) {
		this.hexWrappers.add(rhw);
	}
	
	public void updateGame(Game game) {
		for (HexWrapper hw : getHexWrappers()) {
			hw.updateGame(game);
		}
	}
}
