package org.joverseer.engine.orders;

import org.joverseer.domain.Order;
import org.joverseer.domain.PopulationCenter;
import org.joverseer.domain.ProductEnum;
import org.joverseer.engine.ErrorException;
import org.joverseer.engine.ExecutingOrder;
import org.joverseer.engine.ExecutingOrderUtils;
import org.joverseer.game.Game;
import org.joverseer.game.Turn;

public class NatTranOrder extends ExecutingOrder {

	public NatTranOrder(Order order) {
		super(order);
	}

	@Override
	public void doExecute(Game game, Turn turn) throws ErrorException {
		int destination = getParameterInt(0);
		checkParamInt(destination, "Invalid destination.");
		String pr = getParameter(1);
		ProductEnum product = ProductEnum.getFromCode(pr);
		if (product == null)
			throw new ErrorException("Invalid product " + pr + ".");
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
		if (!isPopNotSieged())
			return;

		int totalAmountToTransfer = 0;
		int totalComission = 0;
		// compute total amount in pops
		for (PopulationCenter pc : turn.getPopCenters(getNationNo())) {
			if (pc == getPop())
				continue;
			int available = ExecutingOrderUtils.getStores(pc, product);
			if (available == 0)
				continue;
			int toTransfer = available * pct / 100;
			int commission = toTransfer / 10;
			if (toTransfer + commission > available) {
				toTransfer = available - commission;
			}
			totalAmountToTransfer += toTransfer;
			totalComission += commission;
			ExecutingOrderUtils.consumeProduct(pc, product, toTransfer + commission);
		}
		int amountAlreadyInPop = ExecutingOrderUtils.getStores(getPop(), product);

		// add
		getPop2().setStores(product, totalAmountToTransfer + amountAlreadyInPop);
		addMessage((totalAmountToTransfer + amountAlreadyInPop) + " " + product + " were transfered to {pc2}.");

	}

}
