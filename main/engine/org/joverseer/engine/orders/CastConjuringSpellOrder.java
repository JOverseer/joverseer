package org.joverseer.engine.orders;

import org.joverseer.domain.Order;
import org.joverseer.domain.ProductEnum;
import org.joverseer.engine.ErrorException;
import org.joverseer.engine.ExecutingOrder;
import org.joverseer.engine.ExecutingOrderUtils;
import org.joverseer.engine.Randomizer;
import org.joverseer.game.Game;
import org.joverseer.game.Turn;
import org.joverseer.metadata.domain.SpellInfo;

public class CastConjuringSpellOrder extends ExecutingOrder {

	public CastConjuringSpellOrder(Order order) {
		super(order);
	}

	@Override
	public void doExecute(Game game, Turn turn) throws ErrorException {
		addMessage("{char} was ordered to cast a spell.");
		if (!isMage()) return;
		
		if (!loadPopCenter(turn)) {
			addMessage("{gp} could not execute the order because {gp} was not at a population center.");
			return;
		}
		if (!isPopCenterOfNation()) {
			addMessage("{gp} could not execute the order because {gp} was not at a population center.");
			return;
		}
		int spellId = getParameterInt(0);
		if (!loadSpell(spellId)) {
			addMessage("{gp} was not able to cast {spell} because {gp} does not know it.");
			return;
		}
		
		// try cast spell
		int roll = ExecutingOrderUtils.spellCastRoll(getCharacter(), getSpellProficiency());
		boolean success = Randomizer.success(roll);
		if (!success) {
			addMessage("{char} failed to cast the spell.");
			return;
		} else {
			addMessage("{spell} was cast.");
		}
		
		if (spellId == 508) {
			doExecuteConjureMounts(game, turn);
		} else {
			throw new RuntimeException("Spell " + spellId + " not implemented.");
		}
	}
	
	protected void doExecuteConjureMounts(Game game, Turn turn) {
		int target = getParameterInt(1);
		
		int mounts = Math.min(target, getCharacter().getMage() * 5);
		
		int stores = ExecutingOrderUtils.getStores(getPop(), ProductEnum.Mounts);
		stores += mounts;
		getPop().setStores(ProductEnum.Mounts, stores);
		addMessage(mounts + " mounts were conjured.");
	}
	
	

}
