package org.joverseer.engine.orders;

import org.joverseer.domain.NationEconomy;
import org.joverseer.domain.Order;
import org.joverseer.domain.PopulationCenter;
import org.joverseer.domain.ProductEnum;
import org.joverseer.engine.ErrorException;
import org.joverseer.engine.ExecutingOrder;
import org.joverseer.engine.ExecutingOrderUtils;
import org.joverseer.game.Game;
import org.joverseer.game.Turn;

public class TransCarOrder extends ExecutingOrder {

	public TransCarOrder(Order order) {
		super(order);
	}

	@Override
	public void doExecute(Game game, Turn turn) throws ErrorException {
		int originHex = getParameterInt(0);
		int destHex = getParameterInt(1);
		String pr = getParameter(2);
		int amount = getParameterInt(3);
		
		if (!loadPopCenter(turn, originHex)) {
			addMessage("No population center was found at hex " + originHex +".");
		}
		if (!loadPopCenter2(turn, destHex) || getPop2().getHidden()) {
			throw new ErrorException("No population center was found at hex " + destHex +".");
		}
		ProductEnum product = ProductEnum.getFromCode(pr);
		if (product == null) {
			throw new ErrorException("Invalid product code " + pr + ".");
		}
		addMessage("{char} was ordered to transfer some products.");
		if (!isPopCenterOfNation()) {
			addMessage("{gp} was unable to transfer the products because {pc} was not of the same nation.");
			return;
		}
		if (!isPopNotSieged()) return;
		
		//TODO check siege
		int stores;
		boolean goldTransfer = product.equals(ProductEnum.Gold);
		NationEconomy originNE = null;
		NationEconomy destNE = null;
		if (goldTransfer) {
			originNE = ExecutingOrderUtils.getNationEconomy(turn, getNationNo());
			destNE = ExecutingOrderUtils.getNationEconomy(turn, getPop2().getNationNo());
			stores = originNE.getAvailableGold();
		} else {
			stores = ExecutingOrderUtils.getStores(getPop(), product);
		}
		int realAmount = Math.min(amount, stores);
		int commission = realAmount / 10;
		if (stores < realAmount + commission) {
			realAmount = stores - commission;
		}
		addMessage(realAmount + " units of " + product + " were transfered to {pc2}.");
		addMessage(commission + " units of " + product + " were spent as commission.");
		
		if (goldTransfer) {
			originNE.addAvailableGold(-realAmount);
			destNE.addAvailableGold(realAmount);
		} else {
			ExecutingOrderUtils.consumeProduct(getPop(), product, realAmount + commission);
			int currentStores = ExecutingOrderUtils.getStores(getPop2(), product);
			getPop2().setStores(product, currentStores + realAmount);
		} 
	}

}
