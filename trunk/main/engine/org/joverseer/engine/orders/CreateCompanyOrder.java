package org.joverseer.engine.orders;

import org.joverseer.domain.Company;
import org.joverseer.domain.Order;
import org.joverseer.engine.ErrorException;
import org.joverseer.engine.ExecutingOrder;
import org.joverseer.engine.ExecutingOrderUtils;
import org.joverseer.game.Game;
import org.joverseer.game.Turn;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.ui.command.AddEditNoteCommand;

public class CreateCompanyOrder extends ExecutingOrder {

	public CreateCompanyOrder(Order order) {
		super(order);
	}

	@Override
	public void doExecute(Game game, Turn turn) throws ErrorException {
		// check not with company
		if (isArmyCommander(turn)) {
			addMessage("{char} could not form a company because {gp} commands an army.");
		}
		if (isCompanyCommander(turn)) {
			addMessage("{char} could not form a company because {gp} commands a company.");
		}
		if (!isCommander()) return;
		
		removeCharacterFromGroups(turn);

		Company c = new Company();
		c.setCommander(getCharacter().getName());
		c.setHexNo(getHex());
		c.setInfoSource(getInfoSource(turn));
		turn.getContainer(TurnElementsEnum.Company).addItem(c);
		addMessage("{char} was ordered to form a company.");
		addMessage("{char} formed a new company.");
	}

	
}
