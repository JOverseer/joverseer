package org.joverseer.engine.orders;

import org.joverseer.domain.Order;
import org.joverseer.engine.ErrorException;
import org.joverseer.engine.ExecutingOrder;
import org.joverseer.engine.Randomizer;
import org.joverseer.game.Game;
import org.joverseer.game.Turn;

public class GuardPopOrder extends ExecutingOrder {

	public GuardPopOrder(Order order) {
		super(order);
	}

	@Override
	public void doExecute(Game game, Turn turn) throws ErrorException {
		addMessage("{char} was ordered to guard the population center.");
		if (!isAgent()) return;
		
		if (!loadPopCenter(turn)) {
			addMessage("{char} was unable to execute the order because {gp} was not at a population center.");
			return;
		}
		
		addMessage("{pc} was guarded.");
		
		int si = Randomizer.roll(1, 5);
		
		modifyProperty(getCharacter(), "agent", si, 1, 1000);
		modifyProperty(getCharacter(), "agentTotal", si, 1, 1000);
		addMessage("{char}'s agent rank was improved by " + si + " points.");
	}
	
	

}
