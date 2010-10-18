package org.joverseer.engine.orders;

import org.joverseer.domain.Order;
import org.joverseer.engine.ErrorException;
import org.joverseer.engine.ExecutingOrder;
import org.joverseer.engine.ExecutingOrderUtils;
import org.joverseer.game.Game;
import org.joverseer.game.Turn;

public class TransferPopOrder extends ExecutingOrder {

	
	public TransferPopOrder(Order order) {
		super(order);
	}

	@Override
	public void doExecute(Game game, Turn turn) throws ErrorException {
		String charId = getParameter(0);
		
		addMessage("{char} was ordered to transfer ownership of the population center.");
		
		if (!isEmissary()) {
			addMessage("{char} was unable to transfer ownership of the population center because {gp} is not an emissary.");
			return;
		}

		if (!loadPopCenter(turn)) {
			addMessage("{char} was unable to transfer ownership of the population center, because no population center exists at {startHex}.");
			return;
		}
		
		if (!isPopCenterOfNation()) {
			addMessage("{char} was unable to transfer ownership of the population center, because the population center is not of the same nation.");
			return;
		}
		
		if (getPop().getHidden()) {
			addMessage("{char} was unable to transfer ownership of the population center, because {pc} is hidden.");
			return;
		}
		
		if (getPop().getCapital()) {
			addMessage("{char} was unable to transfer ownership of the population center, because {pc} is the capital.");
			return;
		}
		
		if (!loadCharacter2(turn, charId)) {
			addMessage("{char} was unable to transfer ownership of the population center, because no character with id " + charId + " exists.");
			return;
		}
		
		if (!areCharsAtSameHex()) {
			addMessage("{char} was unable to transfer ownership of the population center to {char2}, because the characters were not at the same hex.");
			return;
		}
		
		if (getCharacter().getNationNo().equals(getCharacter2().getNationNo())) {
			addMessage("{char} was unable to transfer ownership of the population center to {char2}, because the characters are of the same nation.");
			return;
		}
		
		if (!ExecutingOrderUtils.checkNonHostile(turn, getCharacter(), getCharacter2())) {
			addMessage("{char} was unable to transfer ownership of the population center to {char2}, because the two nations are hostile.");
			return;
		}
		
		if (getCharacter2().getEmmisary() == 0) {
			addMessage("{char} was unable to transfer ownership of the population center to {char2}, because {char2} is not an emissary.");
			return;
		}
		
		
		
		int newLoyalty = getCharacter2().getEmmisaryTotal() / 2;
		
		getPop().setNationNo(getCharacter2().getNationNo());
		getPop().setLoyalty(newLoyalty);
		
		addMessage("Ownership of {pc} was transfered.");
	}

}
