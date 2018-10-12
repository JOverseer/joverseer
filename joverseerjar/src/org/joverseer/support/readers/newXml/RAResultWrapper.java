package org.joverseer.support.readers.newXml;

import java.util.ArrayList;

import org.joverseer.game.Game;
import org.joverseer.game.Turn;
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
	@Override
	public void updateGame(Game game, Turn turn, int nationNo, String character) {
		ArtifactInfo ai;
		for(ArtifactWrapper aw:getArtifacts()) {
			ai = (ArtifactInfo)game.getMetadata().getArtifacts().findFirstByProperty("name", aw.getName());
			if (ai != null && ai.getNo() == 0) {
				ai.setNo(aw.getId());
			}
		}
	}
	
	
}
