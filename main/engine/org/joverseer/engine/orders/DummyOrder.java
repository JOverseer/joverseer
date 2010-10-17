package org.joverseer.engine.orders;

import org.joverseer.domain.Order;
import org.joverseer.engine.ExecutingOrder;
import org.joverseer.game.Game;
import org.joverseer.game.Turn;

public class DummyOrder extends ExecutingOrder {

	public DummyOrder(Order order) {
		super(order);
	}

	@Override
	public void doExecute(Game game, Turn turn) {
		// do nothing
		addMessage("{char} was ordered to execute order " + getOrderNo() + ".");
		addMessage("The order was ignored.");
	}

	
}
