package org.joverseer.engine.orders;

import java.util.ArrayList;

import org.joverseer.domain.Order;
import org.joverseer.domain.PopulationCenter;
import org.joverseer.domain.ProductEnum;
import org.joverseer.engine.ErrorException;
import org.joverseer.engine.ExecutingOrder;
import org.joverseer.engine.ExecutingOrderUtils;
import org.joverseer.game.Game;
import org.joverseer.game.Turn;
import org.joverseer.game.TurnElementsEnum;

public class NatTranOrder extends ExecutingOrder{

	public NatTranOrder(Order order) {
		super(order);
	}

	@Override
	public void doExecute(Game game, Turn turn) throws ErrorException {
		int destination = getParameterInt(0);
		checkParamInt(destination, "Invalid destination.");
		String pr = getParameter(1);
		ProductEnum product = ProductEnum.getFromCode(pr);
		if (product == null) throw new ErrorException("Invalid product " + pr + ".");
		int pct = getParameterInt(2);
		checkParamInt(pct, "Invalid percentage.");
		
		addMessage("{char} was ordered to transfer " + product + ".");
		if (!loadPopCenter2(turn, destination)) {
			addMessage("There is no population center at " + destination + ".");
			return;
		}
		if (!isPopCenter2OfNation()) {
			addMessage("{char} was not able to perform the transfer because {pc} was not of the same nation.");
			return;
		}
		if (!loadPopCenter(turn) || !isAtCapital()) {
			addMessage("{char} was not able to perform the transfer because he was not at the capital.");
		}
		if (!isPopNotSieged()) return;
		
		// compute total amount in pops
		// TODO handle sieged
		int totalAmount = ExecutingOrderUtils.getTotalStoresForNation(turn, product, getNationNo());

		int amountAlreadyInPop = ExecutingOrderUtils.getStores(getPop2(), product);

		int totalAmountToTransfer = totalAmount - amountAlreadyInPop;
		
		// transfer amount
		int transferAmount = totalAmountToTransfer * pct / 100;
		
		// commission
		int commission = transferAmount / 10;
		if (transferAmount + commission > totalAmount) {
			transferAmount = totalAmountToTransfer - commission;
		}
		// compute the actual percentage consumed based on the commission and available amount
		int realPct = (transferAmount + commission) * 100 / totalAmount;
		// consume
		ExecutingOrderUtils.consumeProductPercentageForNation(turn, product, realPct, getNationNo());
		
		// add
		getPop2().setStores(product, transferAmount + amountAlreadyInPop);
		addMessage((transferAmount + amountAlreadyInPop) + " " + product + " were transfered to {pc2}.");
		
	}

}
