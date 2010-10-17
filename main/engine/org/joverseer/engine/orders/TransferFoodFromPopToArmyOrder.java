package org.joverseer.engine.orders;

import org.joverseer.domain.Order;
import org.joverseer.domain.ProductEnum;
import org.joverseer.engine.ErrorException;
import org.joverseer.engine.ExecutingOrder;
import org.joverseer.engine.ExecutingOrderUtils;
import org.joverseer.game.Game;
import org.joverseer.game.Turn;

public class TransferFoodFromPopToArmyOrder extends ExecutingOrder {

	public TransferFoodFromPopToArmyOrder(Order order) {
		super(order);
	}

	@Override
	public void doExecute(Game game, Turn turn) throws ErrorException {
		int food = getParameterInt(0);
		
		addMessage("{char} was ordered to transfer some food to the army.");
		if (!loadArmyByMember(turn)) {
			addMessage("{char} was unable to transfer food because he was not with an army.");
			return;
		}
		if (!loadPopCenter(turn)) {
			addMessage("{char} was unable to transfer food because he was not at a population center.");
			return;
		}
		if (!ExecutingOrderUtils.checkFriendly(turn, getPop(), getCharacter())) {
			addMessage("{char} was unable to transfer food because the population center did not have friendly relations to the army.");
			return;
		}
		if (getPop().getHidden() && !ExecutingOrderUtils.checkNation(getPop(), getCharacter())) {
			addMessage("{char} was unable to transfer food because he was not at a population center.");
			return;
		}
		if (!isPopNotSieged()) return;
		
		int availableFood = ExecutingOrderUtils.getStores(getPop(), ProductEnum.Food);
		if (availableFood == 0) {
			addMessage("{char} was unable to transfer food because the population center did not have any food.");
			return;
		}
		
		int foodToTransfer = Math.min(availableFood, food);
		if (foodToTransfer < food) {
			addMessage("The amount of food transfered was adjusted because there was not enough food at the population center.");
		}
		ExecutingOrderUtils.consumeProduct(getPop(), ProductEnum.Food, foodToTransfer);
		Integer f = getArmy().getFood();
		if (f == null) f = 0;
		getArmy().setFood(f + foodToTransfer);
		addMessage(foodToTransfer + " food units were transfered to the army.");
	}
}
