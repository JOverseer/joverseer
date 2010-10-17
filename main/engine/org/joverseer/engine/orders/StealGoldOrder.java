package org.joverseer.engine.orders;

import org.joverseer.domain.CharacterDeathReasonEnum;
import org.joverseer.domain.Order;
import org.joverseer.domain.ProductEnum;
import org.joverseer.engine.ErrorException;
import org.joverseer.engine.ExecutingOrder;
import org.joverseer.engine.ExecutingOrderUtils;
import org.joverseer.engine.Randomizer;
import org.joverseer.game.Game;
import org.joverseer.game.Turn;

public class StealGoldOrder extends ExecutingOrder {

	public StealGoldOrder(Order order) {
		super(order);
	}

	@Override
	public void doExecute(Game game, Turn turn) throws ErrorException {
		addMessage("{char} was ordered to steal gold.");
		
		if (!isAgent()) return;
		
		if (!loadPopCenter(turn)) {
			addMessage("{char} was unable to steal gold because {gp} was not at a population center.");
			return;
		}
		
		if (isPopCenterOfNation()) {
			addMessage("{char} was unable to steal gold because {gp} the population center was of the same nation.");
			return;
		}
		
		//TODO guards
		
		int skillMod = getCharacter().getAgentTotal();
		int loyaltyMod = 70 - getPop().getLoyalty();
		int fortMod = - getPop().getFortification().getSize() * 5;
		
		int roll = Randomizer.roll(skillMod + loyaltyMod + fortMod);
		if (Randomizer.success(roll)) {
			int skillIncrease = Randomizer.roll(1, 10);
			modifyProperty(getCharacter(), "agent", skillIncrease, 1, 1000);
			modifyProperty(getCharacter(), "agentTotal", skillIncrease, 1, 1000);
			
			int taxRevenue = ExecutingOrderUtils.getRevenue(turn, getPop());
			int goldProduction = ExecutingOrderUtils.getProduction(getPop(), ProductEnum.Gold);
			int stolenPct = Math.min(skillMod + 30 + Randomizer.roll(-15, 15), 100);
			int stolenGold = (taxRevenue + goldProduction) * stolenPct / 100;
			int availableGold = ExecutingOrderUtils.getAvailableGold(turn, getPop().getNationNo());
			if (availableGold < stolenGold) {
				stolenGold = availableGold;
			}
			if (stolenGold == 0) {
				addMessage("No gold was found.");
			} else {
				addMessage("{char} stole " + stolenGold + " gold.");
			}
			addMessage("{char}'s agent rank was improved by " + skillIncrease + " points.");
			ExecutingOrderUtils.consumeGold(turn, getPop().getNationNo(), stolenGold);
			ExecutingOrderUtils.consumeGold(turn, getNationNo(), -stolenGold);
		} else {
			addMessage("{char}'s attempt to steal gold was thwarted by the local militia.");
			// chance of injury
			int cr = Randomizer.roll(skillMod);
			int lr = Randomizer.roll(getPop().getLoyalty() - fortMod);
			if (lr > cr) {
				//injured
				modifyProperty(getCharacter(), "health", cr - lr, 0, 100);
				if (getCharacter().getHealth() > 0) {
					addMessage("{char} was injured.");
				} else {
					addMessage("{char} was killed.");
					ExecutingOrderUtils.characterDied(turn, getCharacter(), CharacterDeathReasonEnum.Dead);
				}
			}
		}
		

	}
	
	

}
