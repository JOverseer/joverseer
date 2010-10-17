package org.joverseer.engine.orders;

import org.joverseer.domain.NationEconomy;
import org.joverseer.domain.Order;
import org.joverseer.engine.ErrorException;
import org.joverseer.engine.ExecutingOrder;
import org.joverseer.engine.ExecutingOrderUtils;
import org.joverseer.game.Game;
import org.joverseer.game.Turn;

public class ChangeTaxRateOrder extends ExecutingOrder {

	public ChangeTaxRateOrder(Order order) {
		super(order);
	}

	@Override
	public void doExecute(Game game, Turn turn) throws ErrorException {
		int newTaxRate = getParameterInt(0);
		
		addMessage("{char} was ordered to change the tax rate.");
		if (!loadPopCenter(turn) || !isAtCapital()) {
			addMessage("{char} could not change the tax rate because he was not at the capital.");
			return;
		}
		if (!isCommander()) {
			return;
		}
		
		//TODO use Randomizer
		NationEconomy ne = ExecutingOrderUtils.getNationEconomy(turn, getNationNo());
		ne.setTaxRate(newTaxRate);
		addMessage("The tax rate was changed to " + newTaxRate + ".");
	}
	

}
