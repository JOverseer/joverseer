package org.joverseer.engine.orders;

import org.joverseer.domain.Order;
import org.joverseer.engine.ErrorException;
import org.joverseer.engine.ExecutingOrder;
import org.joverseer.engine.ExecutingOrderUtils;
import org.joverseer.game.Game;
import org.joverseer.game.Turn;
import org.joverseer.ui.command.AddEditNoteCommand;

public class JoinArmyOrder extends ExecutingOrder {

	public JoinArmyOrder(Order order) {
		super(order);
	}

	@Override
	public void doExecute(Game game, Turn turn) throws ErrorException {
		String id = "";
		int hexNo = 0;
		if (getOrderNo() == 785) {
			id = getParameter(0);
		} else if (getOrderNo() == 870) {
			hexNo = getParameterInt(0);
			id = getParameter(1);
		}
		
		addMessage("{char} was ordered to join the army commanded by " + id +".");
		if (isArmyCommander(turn)) {
			addMessage("{char} could not join the army because {gp} is an army commander.");
			return;
		}
		if (isCompanyCommander(turn)) {
			addMessage("{char} could not join the army because {gp} is a company commander.");
			return;
		}
		if (!loadCharacter2(turn, id)) {
			addMessage("{char} could not join the army because no character with id " + id + " was found.");
			return;
		}
		if (!areCharsOfSameNation()) {
			addMessage("{char} could not join the army because {char2} was not of the same nation.");
			return;
		}
		if (getOrderNo() == 785) {
			if (!areCharsAtSameHex()) {
				addMessage("{char} could not join the army because {char2} was not in the same hex.");
				return;
			}
		}
		if (!loadArmy2ByCommander(turn)) {
			addMessage("{char} could not join the army because {char2} does not command an army.");
			return;
		}
		
		removeCharacterFromGroups(turn);

		if (getOrderNo() != 785) {
			getCharacter().setHexNo(hexNo);
			ExecutingOrderUtils.refreshCharacter(turn, getCharacter());
		}
		
		getArmy2().getCharacters().add(getName());
		addMessage("{char} joined the army commanded by {char2}.");
	}
	
	

}
