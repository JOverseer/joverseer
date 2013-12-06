package org.joverseer.tools.ordercheckerIntegration;

import java.util.ArrayList;

import org.joverseer.domain.Character;
import org.joverseer.domain.CharacterDeathReasonEnum;
import org.joverseer.domain.Order;
import org.joverseer.game.Game;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.support.GameHolder;

/**
 * Performs additional checks on the orders and adds the results to the order
 * checker results
 * 
 * @author Marios Skounakis
 */

// TODO this class is not being used right now
public class OrdercheckerPostprocessor {
	OrdercheckerProxy proxy;

	public OrdercheckerProxy getProxy() {
		return this.proxy;
	}

	public void setProxy(OrdercheckerProxy proxy) {
		this.proxy = proxy;
	}

	public void runChecks() {
		Game g = GameHolder.instance().getGame();
		int nationNo = this.proxy.getNationNo();
		ArrayList<Order> orders = new ArrayList<Order>();
		ArrayList<Character> chars = g.getTurn().getContainer(TurnElementsEnum.Character).findAllByProperty("nationNo", nationNo);
		for (Character c : chars) {
			if (c.getDeathReason() != CharacterDeathReasonEnum.NotDead)
				continue;
			for (Order o : c.getOrders()) {
				if (o.isBlank())
					continue;
				orders.add(o);
			}
		}

		// check natsell orders and materials use
	}
}
