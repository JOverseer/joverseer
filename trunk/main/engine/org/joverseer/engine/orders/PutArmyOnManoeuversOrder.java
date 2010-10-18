package org.joverseer.engine.orders;

import org.joverseer.domain.ArmyElement;
import org.joverseer.domain.Character;
import org.joverseer.domain.Order;
import org.joverseer.engine.ErrorException;
import org.joverseer.engine.ExecutingOrder;
import org.joverseer.engine.ExecutingOrderUtils;
import org.joverseer.engine.Randomizer;
import org.joverseer.game.Game;
import org.joverseer.game.Turn;

public class PutArmyOnManoeuversOrder extends ExecutingOrder {

	public PutArmyOnManoeuversOrder(Order order) {
		super(order);
	}

	@Override
	public void doExecute(Game game, Turn turn) throws ErrorException {
		boolean army = getOrderNo() == 435;
		String troopType = "";
		Character ch = null;
		String command = "the army";
		if (!army) {
			troopType = getParameter(0);
			command = "the troops";
			ch = getCharacter();
		}
		
		if (army) {
			addMessage("{char} was ordered to put the army on manoeuvers");
		} else {
			addMessage("{char} was ordered to put some troops on manoeuvers");
		}
		
		if (!isCommander()) return;
		if (army) {
			if(!loadArmyByCommander(turn)) {
				addMessage("{gp} was unable to put "+ command + " because {gp} did not command an army.");
			}
		} else if (!loadArmyByMember(turn)) {
			addMessage("{gp} was unable to put "+ command + " because {gp} was not with an army.");
		}
		
		//TODO check land
		
		addMessage("{char} put " + command + " on manoeuvers.");
		
		int trmax;
		int csmax;
		if (army) {
			trmax = 5;
			csmax = 5;
		} else {
			trmax = 10;
			csmax = 7;
		}
		
		
		int r = Randomizer.roll(1, trmax);
		
		// improve troop taining
		for (ArmyElement ae : getArmy().getElements()) {
			if (ae.getArmyElementType().isTroop()) {
				if (troopType.equals("") || troopType.toUpperCase().equals(ae.getArmyElementType().getType())) {
					modifyProperty(ae, "training", r, 1, 100);
				}
			}
		}
		
		for (Character c : ExecutingOrderUtils.getCharsWithArmy(turn, getArmy(), true)) {
			if (ch == null || ch == c) {
				// improve char rank
				r = Randomizer.roll(1, csmax);
				if (ch == c) {
					addMessage("{char}'s command rank was improved by " + r + " points");
				}
				modifyProperty(c, "command", r, 1, 1000);
			}
		}
	}
	
	

}
