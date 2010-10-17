package org.joverseer.engine.orders;

import org.joverseer.domain.Character;
import org.joverseer.domain.Company;
import org.joverseer.domain.Order;
import org.joverseer.engine.ErrorException;
import org.joverseer.engine.ExecutingOrder;
import org.joverseer.engine.ExecutingOrderUtils;
import org.joverseer.game.Game;
import org.joverseer.game.Turn;

public class MoveCompanyOrder extends ExecutingOrder {

	public MoveCompanyOrder(Order order) {
		super(order);
	}

	@Override
	public void doExecute(Game game, Turn turn) throws ErrorException {
		addMessage("{char} was order to move the company.");
		if (!loadCompanyByCommander(turn)) {
			addMessage("{char} was unable to move the company because he did not command a company.");
			return;
		}
		int destination = getParameterInt(0);
		checkParamInt(destination, "Invalid destination.");
		setEndHex(destination);
		addMessage("{gp} moved the company to {end}.");
		for (Character c : ExecutingOrderUtils.getCharsWithCompany(turn, getCompany(), true)) {
			c.setHexNo(destination);
			ExecutingOrderUtils.refreshCharacter(turn, c);
		}
	}

}
