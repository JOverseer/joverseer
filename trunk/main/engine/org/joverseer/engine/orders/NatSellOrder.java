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

public class NatSellOrder extends ExecutingOrder {

	public NatSellOrder(Order order) {
		super(order);
	}

	@Override
	public void doExecute(Game game, Turn turn) throws ErrorException {
		String pr = getParameter(0);
		ProductEnum product = ProductEnum.getFromCode(pr);
		if (product == null) throw new ErrorException("Invalid product " + pr + ".");
		int pct = getParameterInt(1);
		checkParamInt(pct, "Invalid percentage.");
		
		addMessage("{char} was ordered to sell some " + product + " to the market.");
		loadPopCenter(turn);
		if (!isAtCapital()) {
			addMessage("{char} was not able to perform the sell because he was not at the capital.");
		}
		if (!isPopNotSieged()) return;
		
		int totalAmount = ExecutingOrderUtils.getTotalStoresForNation(turn, product, getNationNo());
		int sellAmount = totalAmount * pct / 100;
		
		ProductPrice pp = ExecutingOrderUtils.getProductPrice(turn, product);
		
		int gain = sellAmount * pp.getSellPrice();
		if (gain > 20000) { //TODO flexible market limit
			sellAmount = 20000 / pp.getSellPrice();
			//TODO market limit for all nation's nat sells
			addMessage("The amount sold was adjusted because the market could not absorb all of it.");
			pct = sellAmount * 100 / totalAmount;
			gain = sellAmount * pp.getSellPrice();
		}
		
		ExecutingOrderUtils.consumeProductPercentageForNation(turn, product, pct, getNationNo());
		
		NationEconomy ne = ExecutingOrderUtils.getNationEconomy(turn, getNationNo());
		
		ne.addAvailableGold(gain);
		addMessage(sellAmount + " " + product + " were sold for " + gain + " gold.");
		
	}

	
}
