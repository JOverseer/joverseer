package org.joverseer.engine.orders;

import org.joverseer.domain.Character;
import org.joverseer.domain.CharacterDeathReasonEnum;
import org.joverseer.domain.Order;
import org.joverseer.engine.ErrorException;
import org.joverseer.engine.ExecutingOrder;
import org.joverseer.engine.ExecutingOrderUtils;
import org.joverseer.game.Game;
import org.joverseer.game.Turn;
import org.joverseer.ui.listviews.renderers.DeathReasonEnumRenderer;

public class MoveCharacterOrder extends ExecutingOrder {

	public MoveCharacterOrder(Order order) {
		super(order);
	}
	
	@Override
	public void doExecute(Game game, Turn turn) throws ErrorException {
		if (isArmyCommander(turn)) {
			addMessage("{char} was not able to move because {gp} commands an army.");
			return;
		}
		if (isCompanyCommander(turn)) {
			addMessage("{char} was not able to move because {gp} commands a company.");
			return;
		}
		if (getCharacter().getDeathReason().equals(CharacterDeathReasonEnum.NotDead)) {
			
			removeCharacterFromGroups(turn);
			
			int destination = getOrder().getParameterInt(0);
			checkParamInt(destination, "Invalid destination.");
			getCharacter().setHexNo(destination);
			setEndHex(destination);
			ExecutingOrderUtils.refreshCharacter(turn, getCharacter());
			addMessage("{char} moved to {endhex}.");
		} else {
			addMessage("{char} could not move to {endhex} because {char} is dead.");
		}
	}
	
}
