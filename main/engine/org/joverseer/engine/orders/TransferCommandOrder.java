package org.joverseer.engine.orders;

import org.joverseer.domain.Army;
import org.joverseer.domain.ArmyElement;
import org.joverseer.domain.ArmyElementType;
import org.joverseer.domain.Character;
import org.joverseer.domain.Company;
import org.joverseer.domain.Order;
import org.joverseer.engine.ErrorException;
import org.joverseer.engine.ExecutingOrder;
import org.joverseer.engine.ExecutingOrderUtils;
import org.joverseer.game.Game;
import org.joverseer.game.Turn;
import org.joverseer.game.TurnElementsEnum;

public class TransferCommandOrder extends ExecutingOrder {

	public TransferCommandOrder(Order order) {
		super(order);
	}

	@Override
	public void doExecute(Game game, Turn turn) throws ErrorException {
		String destId = getParameter(0);
		boolean join = getParameter(1).equals("y");
		
		addMessage("{char} was ordered to transfer command of his army.");
		if (!loadArmyByCommander(turn)) {
			addMessage("{char} was not able to transfer command because he did not command an army.");
			return;
		}

		if (!loadCharacter2(turn, destId)) {
			addMessage("{char} was not able to transfer command because no character with id " + destId + " present.");
			return;
		}
		if (!areCharsOfSameNation()) {
			addMessage("{char} was not able to transfer command because {char2} was not of the same nation.");
			return;
		}
		if (!areCharsAtSameHex()) {
			addMessage("{char} was not able to transfer command because {char2} was not in the same hex.");
			return;
		}
		if (isCompanyCommander2(turn)) {
			addMessage("{char} was not able to transfer command because {char2} is a company commander.");
			return;
		}
		
		removeCharacter2FromGroups(turn);
		
		Army destArmy = ExecutingOrderUtils.getArmy(turn, getCharacter2().getHexNo(), getCharacter2().getName());
		if (destArmy == null) {
			destArmy = getArmy();
			destArmy.setCommanderName(getCharacter2().getName());
		} else {
			for (ArmyElement ae : getArmy().getElements()) {
				ArmyElement dae = destArmy.getElement(ae.getArmyElementType());
				if (dae == null) {
					destArmy.setElement(ae);
				} else {
					dae.mergeWith(ae);
				}
			}
			Integer destFood = destArmy.getFood();
			if (destFood == null) destFood = 0;
			Integer food = getArmy().getFood();
			if (food == null) food = 0;
			destFood += food;
			destArmy.setFood(destFood);
			turn.getContainer(TurnElementsEnum.Army).removeItem(getArmy());
			//TODO handle destArmy morale
		}
		addMessage("{char} transfered command of his army to {char2}.");
		if (join) {
			addMessage("{char} joined the army.");
			destArmy.getCharacters().add(getCharacter().getName());
		}
	}
	
	

}
