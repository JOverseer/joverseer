package org.joverseer.engine.orders;

import org.joverseer.domain.FortificationSizeEnum;
import org.joverseer.domain.Order;
import org.joverseer.domain.PopulationCenterSizeEnum;
import org.joverseer.domain.ProductEnum;
import org.joverseer.engine.ErrorException;
import org.joverseer.engine.ExecutingOrder;
import org.joverseer.engine.ExecutingOrderUtils;
import org.joverseer.game.Game;
import org.joverseer.game.Turn;
import org.joverseer.support.info.InfoUtils;
import org.joverseer.tools.orderCostCalculator.OrderCostCalculator;

public class FortifyPopOrder extends ExecutingOrder {

	public FortifyPopOrder(Order order) {
		super(order);
	}

	@Override
	public void doExecute(Game game, Turn turn) throws ErrorException {
		addMessage("{char} was ordered to fortify the population center.");
		
		if (!isCommander()) {
			return;
		}
		
		if (!loadPopCenter(turn)) {
			addMessage("{char} was unable to fortify the population center because no population center was found.");
			return;
		}
		
		if (!isPopCenterOfNation()) {
			addMessage("{char} was unable to fortify the population center because the population center was not of the same nation.");
			return;
		}
		
		if (getPop().getFortification().equals(FortificationSizeEnum.citadel)) {
			addMessage("{char} was unable to fortify the population center because it is already a citadel.");
			return;
		}
		
		if (getPop().getSize().equals(PopulationCenterSizeEnum.ruins)) {
			addMessage("{char} was unable to fortify the population center because it is ruins.");
			return;
		}
		
		if (!isPopNotSieged()) return;
		
		OrderCostCalculator calc = new OrderCostCalculator();
		int cost = calc.fortifyPopCenterCost(getOrder());
		int timberCost = calc.getContainer().getProduct(ProductEnum.Timber);
		
		if (!ExecutingOrderUtils.hasAvailableProduct(getPop(), ProductEnum.Timber, timberCost)) {
			addMessage("{char} was unable to fortify {pc} because there was insufficient timber.");
			return;
		}
		
		consumeCost(game, turn);
		
		ExecutingOrderUtils.consumeProduct(getPop(), ProductEnum.Timber, timberCost);

		getPop().setFortification(FortificationSizeEnum.getFromSize(getPop().getFortification().getSize() + 1));
		addMessage("The fortifications at {pc} were improved. {pc} is now fortified with a " + getPop().getFortification() + ".");
		
		//TODO add skill increase?
	}
	
	

}
