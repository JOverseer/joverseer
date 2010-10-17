package org.joverseer.engine.orders;

import org.joverseer.domain.Order;
import org.joverseer.engine.ErrorException;
import org.joverseer.engine.ExecutingOrder;
import org.joverseer.game.Game;
import org.joverseer.game.Turn;

public class RefuseChallengesOrder extends ExecutingOrder {

	public RefuseChallengesOrder(Order order) {
		super(order);
	}

	@Override
	public void doExecute(Game game, Turn turn) throws ErrorException {
		addMessage("{char} was ordered to refuse all personal challenges.");
		getCharacter().setRefusingChallenges(true);
	}
	
	public int getSequence() {
		return 210; // switch order with issue challenge
	}

}
