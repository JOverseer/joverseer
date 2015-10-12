package org.joverseer.support.readers.newXml;

import org.joverseer.game.Game;
import org.joverseer.game.Turn;
import org.joverseer.metadata.domain.ArtifactInfo;
import org.joverseer.support.readers.pdf.OrderResult;

public class RAResultWrapper implements OrderResult {
	String artiName;
	String artiNo;
	
	
	public String getArtiName() {
		return this.artiName;
	}
	public void setArtiName(String artiName) {
		this.artiName = artiName;
	}
	public String getArtiNo() {
		return this.artiNo;
	}
	public void setArtiNo(String artiNo) {
		this.artiNo = artiNo;
	}
	@Override
	public void updateGame(Game game, Turn turn, int nationNo, String character) {
		ArtifactInfo ai = (ArtifactInfo)game.getMetadata().getArtifacts().findFirstByProperty("name", getArtiName());
		if (ai != null && ai.getNo() == 0) {
			try {
				ai.setNo(Integer.parseInt(getArtiNo()));
			}
			catch (Exception e) {
				
			}
		}
	}
	
	
}
