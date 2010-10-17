package org.joverseer.engine.orders;

import org.joverseer.domain.Order;
import org.joverseer.engine.ErrorException;
import org.joverseer.engine.ExecutingOrder;
import org.joverseer.engine.ExecutingOrderUtils;
import org.joverseer.engine.Randomizer;
import org.joverseer.game.Game;
import org.joverseer.game.Turn;
import org.joverseer.metadata.domain.SpellInfo;

public class CastCombatSpellOrder extends ExecutingOrder {

	int spellId = 0;
	
	public CastCombatSpellOrder(Order order) {
		super(order);
	}

	@Override
	public void doExecute(Game game, Turn turn) throws ErrorException {
		int spellId = getParameterInt(0);
		
		SpellInfo spi = (SpellInfo)game.getMetadata().getSpells().findFirstByProperty("number", spellId);
		
		addMessage("{char} was ordered to cast " + spi.getName() + ".");
		
		int sp = ExecutingOrderUtils.getSpellProficiency(getCharacter(), spellId);
		if (sp == 0) {
			addMessage("{gp} was not able to cast " + spi.getName() + " because {gp} does not know it.");
			return;
		}
		
		// try cast spell
		int bonus = getCharacter().getMageTotal() - getCharacter().getMage();
		
		int roll = Randomizer.roll(bonus + sp);
		boolean success = Randomizer.success(roll);
		if (!success) {
			addMessage("{char} failed to cast the spell.");
			return;
		} else {
			addMessage(spi.getName() + " was cast.");
		}
		this.spellId = spellId;
		//TODO implement effect
	}

	public int getSpellId() {
		return spellId;
	}
	
}
