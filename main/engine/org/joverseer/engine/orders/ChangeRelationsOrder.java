package org.joverseer.engine.orders;

import org.joverseer.domain.NationRelations;
import org.joverseer.domain.NationRelationsEnum;
import org.joverseer.domain.Order;
import org.joverseer.engine.ErrorException;
import org.joverseer.engine.ExecutingOrder;
import org.joverseer.engine.Randomizer;
import org.joverseer.game.Game;
import org.joverseer.game.Turn;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.metadata.domain.Nation;
import org.joverseer.metadata.domain.NationAllegianceEnum;

public class ChangeRelationsOrder extends ExecutingOrder {

	public ChangeRelationsOrder(Order order) {
		super(order);
	}

	@Override
	public void doExecute(Game game, Turn turn) throws ErrorException {
		int nationNo = getParameterInt(0);
		
		String command = getOrderNo() == 180 ? "upgrade" : "downgrade";
		Nation n = game.getMetadata().getNationByNum(nationNo);
		addMessage("{char} was ordered to " + command + " our relations with " + n.getName() + ".");
		NationRelations nr = (NationRelations)turn.getContainer(TurnElementsEnum.NationRelation).findFirstByProperty("nationNo", getNationNo());
		NationRelationsEnum nre = nr.getRelationsFor(nationNo);

		if (!isCommander()) {
			return;
		}
		
		if (!loadPopCenter(turn) || !isAtCapital()) {
			addMessage("{char} could not complete the order because {gp} was not at the capital.");
			return;
		}
		
		int roll = Randomizer.roll(getCharacter().getCommandTotal() + 30);
		
		//TODO handle allegiances and stuff
		if (getOrderNo() == 180) {
			if (nre.equals(NationRelationsEnum.Friendly)) {
				addMessage("{char} was unable to upgrade our relations with " + n.getName() + " because our relations were already friendly.");
				return;
			}
			if (nre.equals(NationRelationsEnum.Tolerated)) {
				nre = NationRelationsEnum.Friendly;
			} else if (nre.equals(NationRelationsEnum.Neutral)) {
				nre = NationRelationsEnum.Tolerated;
			}
		} else if (getOrderNo() == 185) {
			if (nre.equals(NationRelationsEnum.Hated)) {
				addMessage("{char} was unable to upgrade our relations with " + n.getName() + " because our relations were already hated.");
				return;
			}
			if (nre.equals(NationRelationsEnum.Disliked)) {
				nre = NationRelationsEnum.Hated;
			} else if (nre.equals(NationRelationsEnum.Neutral)) {
				nre = NationRelationsEnum.Disliked;
			}
		}
		
		
		nr.setRelationsFor(nationNo, nre);
		addMessage("Our relations to " + n.getName() + " were changed to " + nre + ".");
	}
	
	

}
