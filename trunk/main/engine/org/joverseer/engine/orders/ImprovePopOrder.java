package org.joverseer.engine.orders;

import org.joverseer.domain.Order;
import org.joverseer.domain.PopulationCenterSizeEnum;
import org.joverseer.engine.ErrorException;
import org.joverseer.engine.ExecutingOrder;
import org.joverseer.engine.Randomizer;
import org.joverseer.game.Game;
import org.joverseer.game.Turn;

public class ImprovePopOrder extends ExecutingOrder {

	public ImprovePopOrder(Order order) {
		super(order);
	}

	@Override
	public void doExecute(Game game, Turn turn) throws ErrorException {
		addMessage("{char} was ordered to improve the population center.");
		if (getCharacter().getEmmisaryTotal() == 0) {
			addMessage("{char} was unable to improve the population center because {gp} is not an emissary.");
			return;
		}
		if (!loadPopCenter(turn)) { 
			addMessage("{char} was unable to improve the population center because no population center was found in {starthex}.");
			return;
		}
		if (getPop().getSize().equals(PopulationCenterSizeEnum.ruins)) {
			addMessage("{char} was unable to improve the population center because the population center was ruins.");
			return;
		}
		if (!isPopCenterOfNation()) {
			addMessage("{char} was unable to improve the population center because it was not of the same nation.");
			return;
		}
		if (getPop().isImprovedThisTurn()) {
			addMessage("{char} was unable to improve {pc} because the populace did not support it.");
			return;
		}
		if (getPop().getSize().equals(PopulationCenterSizeEnum.city)) {
			addMessage("{char} was unable to improve {pc} because it is a city.");
		}
		
		if (!isPopNotSieged()) return;
		
		int skill = getCharacter().getEmmisaryTotal();
		int modifier = 0;
		int size = getPop().getSize().getCode();
		int loyalty = getPop().getLoyalty();
		if (size == 1) {
			modifier = +10;
		} else if (size == 2) {
			modifier = -5;
		} else if (size == 3) {
			modifier = -20;
		} else if (size == 4) {
			modifier = -35;
		}
		int roll = Randomizer.roll(skill + loyalty + modifier);
		if (Randomizer.success(roll)) {
			
			consumeCost(game, turn);
			
			int si = Randomizer.roll(1, 10);
			getPop().setSize(PopulationCenterSizeEnum.getFromCode(size + 1));
			modifyProperty(getCharacter(), "emmisary", si, 0, 1000);
			modifyProperty(getCharacter(), "emmisaryTotal", si, 0, 1000);
			addMessage("{pc} was improved to a " + getPop().getSize() + ".");
			addMessage("{char}'s emissary rank was improved by " + si + " points.");
		} else {
			addMessage("{char} was unable to improve {pc} because the populace did not support it.");
		}
	}
	
	

}
