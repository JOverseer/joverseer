package org.joverseer.engine.orders;

import org.joverseer.domain.HarborSizeEnum;
import org.joverseer.domain.Order;
import org.joverseer.domain.PopulationCenter;
import org.joverseer.domain.PopulationCenterSizeEnum;
import org.joverseer.domain.ProductEnum;
import org.joverseer.engine.ErrorException;
import org.joverseer.engine.ExecutingOrder;
import org.joverseer.engine.ExecutingOrderUtils;
import org.joverseer.engine.Randomizer;
import org.joverseer.game.Game;
import org.joverseer.game.Turn;

public class AddDocksOrder extends ExecutingOrder {
	boolean harbor;

	public AddDocksOrder(Order order) {
		super(order);
	}

	@Override
	public void doExecute(Game game, Turn turn) throws ErrorException {

		harbor = getOrderNo() == 535; // otherwise it's improve harbor to port

		addMessage("{char} was ordered to add a {docks} to the population center.");

		if (!loadPopCenter(turn)) {
			addMessage("{char} was unable to add a {docks} to the population center, because no population center was found at {startHex}.");
			return;
		}

		if (!isPopCenterOfNation()) {
			addMessage("{char} was unable to add a {docks} to the population center, because the population center is not of the same nation.");
			return;
		}

		if (!isEmissary()) {
			addMessage("{char} was unable to add a {docks} to the population center, because {gp} is not an emissary.");
			return;
		}

		PopulationCenter pc = getPop();
		if (harbor) {
			if (!pc.getHarbor().equals(HarborSizeEnum.none)) {
				addMessage("{char} was unable to add a {docks} to the population center, because the population center already has a harbor or port.");
				return;
			}

			if (pc.getSize().getCode() < PopulationCenterSizeEnum.town.getCode()) {
				addMessage("{char} was unable to add a {docks} to the population center, because the population center is not a town, major town or city.");
				return;
			}
		} else {
			if (pc.getHarbor().equals(HarborSizeEnum.none)) {
				addMessage("{char} was unable to add a {docks} to the population center, because the population center does not have a harbor.");
				return;
			}
			if (pc.getHarbor().equals(HarborSizeEnum.port)) {
				addMessage("{char} was unable to add a {docks} to the population center, because the population center already has a port.");
				return;
			}

			if (pc.getSize().getCode() < PopulationCenterSizeEnum.majorTown.getCode()) {
				addMessage("{char} was unable to add a {docks} to the population center, because the population center is not a major town or city.");
				return;
			}
		}

		// check coastal hex

		// check no hostile armies

		int timberCost = harbor ? 5000 : 7500;
		if (!ExecutingOrderUtils.hasAvailableProduct(pc, ProductEnum.Timber, timberCost)) {
			addMessage("{char} was unable to add a {docks} to the population center, because there is not enough timber in the population center's stores.");
			return;
		}

		int goldCost = harbor ? 2500 : 4000;
		if (ExecutingOrderUtils.getAvailableGold(turn, getNationNo()) < goldCost) {
			addMessage("{char} was unable to add a {docks} to the population center, because there is not enough gold in the nation's treasury.");
			return;
		}

		int roll = Randomizer.roll(getCharacter().getEmmisaryTotal() + 40);
		if (!Randomizer.success(roll)) {
			addMessage("{char} was unable to add {docks} to the population center because the populace did not support it.");
			return;
		}

		pc.setHarbor(HarborSizeEnum.harbor);
		ExecutingOrderUtils.consumeGold(turn, getNationNo(), goldCost);
		ExecutingOrderUtils.consumeProduct(pc, ProductEnum.Timber, timberCost);

		addMessage("A {docks} was added at {pc}.");
	}

	@Override
	public String renderVariable(String msg, String variable, String value) {
		String ret = super.renderVariable(msg, variable, value);
		ret = ret.replace("{docks}", harbor ? "harbor" : "port");
		return ret;
	}

}
