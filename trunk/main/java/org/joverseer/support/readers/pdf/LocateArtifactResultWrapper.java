package org.joverseer.support.readers.pdf;

import org.joverseer.game.Game;

public class LocateArtifactResultWrapper implements OrderResult {
	int hexNo;
	int artifactNo;
	String owner;
	String artifactName;
	
	public String getArtifactName() {
		return artifactName;
	}
	public void setArtifactName(String artifactName) {
		this.artifactName = artifactName;
	}
	public int getArtifactNo() {
		return artifactNo;
	}
	public void setArtifactNo(int artifactNo) {
		this.artifactNo = artifactNo;
	}
	public int getHexNo() {
		return hexNo;
	}
	public void setHexNo(int hexNo) {
		this.hexNo = hexNo;
	}
	public String getOwner() {
		return owner;
	}
	public void setOwner(String owner) {
		this.owner = owner;
	}
	
	public void updateGame(Game game) {
	    
        }
}
