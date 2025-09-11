package org.joverseer.support.readers.newXml;

import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.joverseer.game.Game;
import org.joverseer.game.Turn;
import org.joverseer.metadata.GameMetadata;
import org.joverseer.metadata.domain.ArtifactInfo;
import org.joverseer.support.readers.pdf.OrderResult;

public class RAResultWrapper implements OrderResult {
	ArrayList<ArtifactWrapper> artifacts = new ArrayList<ArtifactWrapper>(7); 
	
	public ArrayList<ArtifactWrapper> getArtifacts() {
		return this.artifacts;
	}
	public void Add(String name,int num) {
		ArtifactWrapper aw = new ArtifactWrapper();
		aw.setName(name);
		aw.setId(num);
		this.artifacts.add(aw);
	}
	public ArtifactWrapper getArtifactMatching(int num) {
		for(ArtifactWrapper aw: this.artifacts ) {
			if (aw.id == num) {
				return aw;
			}
		}
		return null;
	}
	@Override
	public void updateGame(Game game, Turn turn, int nationNo, String character) {
		ArtifactInfo ai;
		GameMetadata gm = game.getMetadata(); 
		for(ArtifactWrapper aw:getArtifacts()) {
			ai = gm.findFirstArtifactByName(aw.getName());
			if (ai != null) {
				if(ai.getNo() == 0) ai.setNo(aw.getId());
				ai.setPower(0, aw.getPower());
				ai.setPower(1, aw.latent);
			} else {
				if (ai == null) {
					// referencing an artifact that we don't know about.
					Logger.getRootLogger().warn("unexpected artifact "+aw.getName()+" from RA char="+character);
				}
			}
		}
	}
	
	
}
