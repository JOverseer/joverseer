package org.joverseer.engine.orders;

import java.util.ArrayList;

import org.joverseer.domain.Character;
import org.joverseer.domain.Order;
import org.joverseer.engine.ErrorException;
import org.joverseer.engine.ExecutingOrder;
import org.joverseer.engine.ExecutingOrderUtils;
import org.joverseer.engine.Randomizer;
import org.joverseer.game.Game;
import org.joverseer.game.Turn;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.metadata.domain.Nation;
import org.joverseer.support.movement.MovementUtils;

import org.joverseer.domain.Army;

public class ReconOrder extends ExecutingOrder {

	public ReconOrder(Order order) {
		super(order);
	}

	@Override
	public void doExecute(Game game, Turn turn) throws ErrorException {
		addMessage("{char} was ordered to recon the area.");
		
		if (!isCommander()) {
			return;
		}
		
		int mod = getCharacter().getCommandTotal();
		int armyMod = 0;
		if (loadArmyByCommander(turn)) {
			armyMod = 20;
		}
		
		for (Army a : (ArrayList<Army>)turn.getContainer(TurnElementsEnum.Army).getItems()) {
			if (a == getArmy()) continue;
			int hex = Integer.parseInt(a.getHexNo());
			if (MovementUtils.distance(getHex(), hex) <= 1) {
				int roll = Randomizer.roll(mod + armyMod + 20);
				if (Randomizer.success(roll)) {
					Character commander = ExecutingOrderUtils.getCommander(turn, a, false);
					int troopCount = a.computeNumberOfMen();
					// introduce random variance
					if (roll < 130) {
						int mod2 = (130 - roll) / 2 + 10;
						int up = Randomizer.roll(1, 100) > 50 ? 1 : -1;
						int modTroops = troopCount * mod2 / 100;
						if (modTroops < 100) modTroops = 100;
						troopCount += modTroops * up;
					}
					troopCount = Math.round(troopCount / 100) * 100;
					Nation n = game.getMetadata().getNationByNum(a.getNationNo());
					String armyReport = "An army of " + n.getName() + " under " + ExecutingOrderUtils.getCharacterTitle(turn, commander) + " " + (commander == null ? "" : commander.getName()) + " with " + troopCount + " men is at " + a.getHexNo() + ".";
					addMessage(armyReport);
				}
			}
		}
		
		
		
	}
	
	

}
