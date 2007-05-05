package org.joverseer.support.readers.pdf;

import org.joverseer.domain.InformationSourceEnum;
import org.joverseer.domain.PopulationCenter;
import org.joverseer.game.Turn;
import org.joverseer.game.TurnElementsEnum;

public class InfluenceOtherResult implements OrderResult {
	String popCenter;
	String loyalty;
	public void updateGame(Turn turn, int nationNo, String character) {
		PopulationCenter pc = (PopulationCenter)turn.getContainer(TurnElementsEnum.PopulationCenter).findFirstByProperty("name", popCenter);
		// TODO
//		if (pc != null) {
//			if (pc.getInformationSource() == InformationSourceEnum.exhaustive || 
//					pc.getInformationSource() == InformationSourceEnum.detailed) return;
//			if (pc.getLoyalty() == 0) {
//				pc.setLoyalty(getLoyaltyAsInt());
//			} else {
//				pc.setLoyalty(Math.min(pc.getLoyalty(), getLoyaltyAsInt()));
//			}
//		}
	}
	
	public String getLoyalty() {
		return loyalty;
	}
	public void setLoyalty(String loyalty) {
		this.loyalty = loyalty;
	}
	public String getPopCenter() {
		return popCenter;
	}
	public void setPopCenter(String popCenter) {
		this.popCenter = popCenter;
	}
	
	
}
