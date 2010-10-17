package org.joverseer.engine.orders;

import org.joverseer.domain.Order;
import org.joverseer.engine.ChallengeFight;
import org.joverseer.engine.ErrorException;
import org.joverseer.engine.ExecutingOrder;
import org.joverseer.game.Game;
import org.joverseer.game.Turn;
import org.joverseer.game.TurnElementsEnum;

import com.sun.org.apache.bcel.internal.generic.LADD;

import sun.awt.geom.AreaOp.AddOp;

public class IssueChallengeOrder extends ExecutingOrder{

	public IssueChallengeOrder(Order order) {
		super(order);
	}

	@Override
	public void doExecute(Game game, Turn turn) throws ErrorException {
		String id = getParameter(0);
		
		addMessage("{char} was ordered to issue a personal challenge.");
		if (getCharacter().isInChallengeFight()) {
			addMessage("{char} was not able to issue the challenge because {gp} was already in another personal challenge.");
			return;
		}
		
		if (getCharacter().isRefusingChallenges()) {
			addMessage("{char} was not able to issue the challenge because {gp} was ordered to refuse all challenges.");
			return;
		}
		
		if (!loadCharacter2(turn, id)) {
			addMessage("{char} was not able to issue the challenge because no character with id " + id + " was found.");
			return;
		}
		
		if (!areCharsAtSameHex()) {
			addMessage("{char} was not able to issue the challenge because {char2} was not at the same location.");
			return;
		}
		
		
		if (getCharacter2().isInChallengeFight()) {
			addMessage("{char} was not able to issue the challenge because {char2} was already in another personal challenge.");
			return;
		}
		
		if (loadArmy2ByCommander(turn)) {
			// character 2 is army commander
			if (!loadArmyByMember(turn)) {
				// character 1 not with an army
				addMessage("{char} was not able to issue the challenge because {char2} is an army commander.");
				return;
			}
		}
		
		if (getCharacter2().isRefusingChallenges()) {
			addMessage("{char} challenged {char2} to personal combat but was refused.");
			if (loadArmy2ByCommander(turn)) {
				getArmy2().setMorale(Math.max(5, getArmy2().getMorale() - 10));
			}
			return;
		}


		getCharacter().setInChallengeFight(true);
		getCharacter2().setInChallengeFight(true);
		
		ChallengeFight cf = new ChallengeFight(getCharacter(), getCharacter2(), getHex());
		turn.getContainer(TurnElementsEnum.ChallengeFights).addItem(cf);
		
		addMessage("{char} issued a personal challenge to {char2}. See combat messages.");
		
	}
	
	public int getSequence() {
		return 215; // switch order with refuse challenge
	}

}
