package org.joverseer.support.readers.pdf;

import org.joverseer.domain.InfoSourceValue;
import org.joverseer.domain.InformationSourceEnum;
import org.joverseer.domain.PopulationCenter;
import org.joverseer.game.Game;
import org.joverseer.game.Turn;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.support.infoSources.DerivedFromInfluenceOtherInfoSource;

/**
 * Holds information about Influence Other order results
 * 
 * @author Marios Skounakis
 */
public class InfluenceOtherResultWrapper implements OrderResult {
	String popCenter;
	String loyalty;
	@Override
	public void updateGame(Game game, Turn turn, int nationNo, String character) {
		PopulationCenter pc = (PopulationCenter)turn.getContainer(TurnElementsEnum.PopulationCenter).findFirstByProperty("name", this.popCenter);
		if (pc != null) {
			if (pc.getInformationSource() == InformationSourceEnum.exhaustive || 
					pc.getInformationSource() == InformationSourceEnum.detailed) return;
			InfoSourceValue loyaltyEstimate = new InfoSourceValue();
			loyaltyEstimate.setValue(getLoyalty());
			DerivedFromInfluenceOtherInfoSource diois = new DerivedFromInfluenceOtherInfoSource(turn.getTurnNo(), nationNo, character, getLoyalty());
			loyaltyEstimate.setInfoSource(diois);
			pc.setLoyaltyEstimate(loyaltyEstimate); 
		}
	}
	
	public String getLoyalty() {
		return this.loyalty;
	}
	public void setLoyalty(String loyalty) {
		this.loyalty = loyalty;
	}
	public String getPopCenter() {
		return this.popCenter;
	}
	public void setPopCenter(String popCenter) {
		this.popCenter = popCenter;
	}
	
	
}
