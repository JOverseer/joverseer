package org.joverseer.engine.orders;

import org.joverseer.domain.Order;
import org.joverseer.engine.ErrorException;
import org.joverseer.engine.ExecutingOrder;
import org.joverseer.engine.Randomizer;
import org.joverseer.game.Game;
import org.joverseer.game.Turn;

public class InfYourOrder extends ExecutingOrder {

	public InfYourOrder(Order order) {
		super(order);
	}

	@Override
	public void doExecute(Game game, Turn turn) throws ErrorException {
		addMessage("{char} was order to influnce loyalty.");
		if (getCharacter().getEmmisaryTotal() == 0) {
			addMessage("{char} was unable to influence loyalty because he is not an emissary.");
			return;
		}
		if (!loadPopCenter(turn)) {
			addMessage("{char} was unable to influence loyalty because there was no pop center at {starthex}.");
		}
		if (!isPopCenterOfNation()) {
			addMessage("{char} was unable to influence loyalty because {pc} was not of the same nation.");
		}
		if (!isPopNotSieged()) return;
		
		int li = Randomizer.roll(1, 5);
		int si = Randomizer.roll(1, 5);
		modifyProperty(getPop(), "loyalty", li, 0, 1000);
		modifyProperty(getCharacter(), "emmisary", si, 0, 100);
		modifyProperty(getCharacter(), "emmisaryTotal", si, 0, 1000);
		addMessage("Loyalty was improved at {pc}.");
		addMessage("{char}'s emissary rank was improved by " + si + " points.");
	}
	
	
	
}
