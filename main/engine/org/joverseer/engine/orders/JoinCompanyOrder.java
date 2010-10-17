package org.joverseer.engine.orders;

import org.joverseer.domain.Army;
import org.joverseer.domain.Character;
import org.joverseer.domain.Company;
import org.joverseer.domain.Order;
import org.joverseer.engine.ErrorException;
import org.joverseer.engine.ExecutingOrder;
import org.joverseer.engine.ExecutingOrderUtils;
import org.joverseer.game.Game;
import org.joverseer.game.Turn;

import com.sun.org.apache.bcel.internal.generic.LADD;

public class JoinCompanyOrder extends ExecutingOrder {

	public JoinCompanyOrder(Order order) {
		super(order);
	}

	@Override
	public void doExecute(Game game, Turn turn) throws ErrorException {
		String id = getParameter(0);
		
		loadCharacter2(turn, id);
		if (getCharacter2() == null) {
			addMessage("{char} could not join the company because a character with id " + id + " was not found.");
		}
		if (!areCharsAtSameHex()) {
			addMessage("{char} could not join the company because he was not in the same hex.");
		}
		if (!ExecutingOrderUtils.checkFriendly(turn, getCharacter(), getCharacter2()) ||
				!ExecutingOrderUtils.checkFriendly(turn, getCharacter2(), getCharacter())) {
			addMessage("{char} could not join the company because not both nations have friendly relations with each other.");
		}
		
		Company toJoin = ExecutingOrderUtils.getCompany(turn, getCharacter2().getName());
		if (toJoin == null) {
			addMessage("{char} could not join the company because {char2} does not command a company.");
		}
		
		if (isArmyCommander(turn)) {
			addMessage("{char} could not join the company because he is an army commander.");
		}
		if (isCompanyCommander(turn)) {
			addMessage("{char} could not join the company because he is a company commander.");
		}

		if (loadArmyByMember(turn)) ExecutingOrderUtils.removeCharacterFromArmy(turn, getArmy(), getCharacter());
		if (loadCompanyByMember(turn)) ExecutingOrderUtils.removeCharacterFromCompany(turn, getCompany(), getCharacter());
		
		toJoin.addMember(getCharacter().getName());
		
		addMessage("{char} joined the company lead by {char2}.");
	}

	
}
