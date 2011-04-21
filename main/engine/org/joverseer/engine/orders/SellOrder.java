package org.joverseer.engine.orders;

import org.joverseer.domain.NationEconomy;
import org.joverseer.domain.Order;
import org.joverseer.domain.ProductEnum;
import org.joverseer.domain.ProductPrice;
import org.joverseer.engine.ErrorException;
import org.joverseer.engine.ExecutingOrder;
import org.joverseer.engine.ExecutingOrderUtils;
import org.joverseer.game.Game;
import org.joverseer.game.Turn;

public class SellOrder extends ExecutingOrder {

	public SellOrder(Order order) {
		super(order);
	}

	@Override
	public void doExecute(Game game, Turn turn) throws ErrorException {
		String pr = getParameter(0);
		ProductEnum product = ProductEnum.getFromCode(pr);
		if (product == null)
			throw new ErrorException("Invalid product " + pr + ".");
		int amt = getParameterInt(1);
		checkParamInt(amt, "Invalid percentage.");

		addMessage("{char} was ordered to sell " + amt + " " + product + " to the market.");
		loadPopCenter(turn);
		if (!isPopCenterOfNation()) {
			addMessage("{char} was not able to perform the sell because {pc} was not of the same nation.");
		}
		if (!isPopNotSieged())
			return;

		int totalAmount = ExecutingOrderUtils.getTotalStoresForNation(turn, product, getNationNo());
		if (totalAmount < amt) {
			addMessage("The amount sold was adjusted to " + totalAmount + " because there were insufficient stores.");
			amt = totalAmount;
		}

		ProductPrice pp = ExecutingOrderUtils.getProductPrice(turn, product);

		int gain = amt * pp.getSellPrice();
		if (gain > 20000) { // TODO flexible market limit
			amt = 20000 / pp.getSellPrice();
			// TODO market limit for all nation's nat sells
			addMessage("The amount sold was adjusted because the market could not absorb all of it.");
			gain = amt * pp.getSellPrice();
		}

		ExecutingOrderUtils.consumeProduct(getPop(), product, amt);
		NationEconomy ne = ExecutingOrderUtils.getNationEconomy(turn, getNationNo());

		ne.addAvailableGold(gain);
		addMessage(amt + " " + product + " were sold for " + gain + " gold.");
	}

}
