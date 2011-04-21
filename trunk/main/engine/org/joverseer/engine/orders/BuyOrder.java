package org.joverseer.engine.orders;

import org.joverseer.domain.NationEconomy;
import org.joverseer.domain.Order;
import org.joverseer.domain.PopulationCenter;
import org.joverseer.domain.ProductEnum;
import org.joverseer.domain.ProductPrice;
import org.joverseer.engine.ErrorException;
import org.joverseer.engine.ExecutingOrder;
import org.joverseer.engine.ExecutingOrderUtils;
import org.joverseer.game.Game;
import org.joverseer.game.Turn;
import org.joverseer.ui.command.AddEditNoteCommand;

public class BuyOrder extends ExecutingOrder {

	public BuyOrder(Order order) {
		super(order);
	}

	@Override
	public void doExecute(Game game, Turn turn) throws ErrorException {
		String pr = getParameter(0);
		int amount = getParameterInt(1);
		
		ProductEnum product = ProductEnum.getFromCode(pr);
		if (product == null) {
			throw new ErrorException("Invalid product code " + pr + ".");
		}
		
		loadPopCenter(turn);
		if (!isPopCenterOfNation()) {
			addMessage("{hex} could not buy any " + product + " because he was not at a population center of his nation.");
		}
		
		if (!isPopNotSieged()) {
			return;
		}
		
		NationEconomy ne = getNationEconomy(turn);
		ProductPrice pp = ExecutingOrderUtils.getProductPrice(turn, product);
		int buyPrice = pp.getBuyPrice();
		
		addMessage("{char} was ordered to buy " + amount + " unit of " + product + " for " + buyPrice + " gold per unit.");
		
		if (getOrderNo() == 310) {
			buyPrice = getParameterInt(2);
		}
		int trueAmount = amount;
		if (amount > pp.getMarketTotal()) {
			trueAmount = pp.getMarketTotal();
			if (trueAmount == 0) {
				addMessage("{hex} could not buy any " + product + " because the market did not have any.");
				return;
			}
		}
				
		int cost = buyPrice * trueAmount;
		int availableGold = ne.getAvailableGold();
		if (availableGold < cost) {
			trueAmount = availableGold / buyPrice;
			cost = trueAmount * buyPrice;
			if (trueAmount == 0) {
				addMessage("{hex} could not buy any " + product + " because there was insufficient gold in the nation's treasury.");
				return;
			}
			addMessage("The amount bought was adjusted because there was insufficient gold.");
		}
		
		ne.addAvailableGold(-cost);
		int stores = ExecutingOrderUtils.getStores(getPop(), product);
		stores += trueAmount;
		getPop().setStores(product, stores);
		addMessage(trueAmount + " units of " + product + " were bought for " + cost + " gold.");
	}
	
	

}
