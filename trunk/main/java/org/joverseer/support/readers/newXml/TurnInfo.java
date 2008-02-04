package org.joverseer.support.readers.newXml;

import org.joverseer.support.Container;

public class TurnInfo {
	Container characters;
	Container popCentres;
	Container armies;
	Container hiddenArtifacts;
	Container nonHhiddenArtifacts;
	Container hexes;
	
	public Container getCharacters() {
		return characters;
	}

	public void setCharacters(Container characters) {
		this.characters = characters;
	}

	public Container getArmies() {
		return armies;
	}

	public void setArmies(Container armies) {
		this.armies = armies;
	}

	public Container getPopCentres() {
		return popCentres;
	}

	public void setPopCentres(Container popCentres) {
		this.popCentres = popCentres;
	}

	

	public Container getHiddenArtifacts() {
		return hiddenArtifacts;
	}

	public void setHiddenArtifacts(Container hiddenArtifacts) {
		this.hiddenArtifacts = hiddenArtifacts;
	}

	public Container getNonHhiddenArtifacts() {
		return nonHhiddenArtifacts;
	}

	public void setNonHhiddenArtifacts(Container nonHhiddenArtifacts) {
		this.nonHhiddenArtifacts = nonHhiddenArtifacts;
	}

	public Container getHexes() {
		return hexes;
	}

	public void setHexes(Container hexes) {
		this.hexes = hexes;
	}
	
	
	
	
}
