package org.joverseer.orders.me;

import java.util.ArrayList;

import org.joverseer.game.Turn;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.orders.AbstractTurnPhaseProcessor;
import org.joverseer.orders.OrderUtils;
import org.joverseer.support.GameHolder;
import org.joverseer.domain.Character;

public class HealingTurnPhaseProcessor extends AbstractTurnPhaseProcessor {

    public HealingTurnPhaseProcessor(String name,GameHolder gameHolder) {
        super(name,gameHolder);
    }

    int healthRecoveryPerTurn = 14;

    public int getHealthRecoveryPerTurn() {
        return this.healthRecoveryPerTurn;
    }

    public void setHealthRecoveryPerTurn(int healthRecoveryPerTurn) {
        this.healthRecoveryPerTurn = healthRecoveryPerTurn;
    }

    @Override
	public void processPhase(Turn t) {
        for (Character c : (ArrayList<Character>)t.getContainer(TurnElementsEnum.Character).getItems()) {
            if (c.getHealth() != null) {
            	int health = c.getHealth().intValue();
            	if (health > 0 && health < 100) {
            		int healAmt = Math.min(100 - health, getHealthRecoveryPerTurn());
            		c.setHealth(Integer.valueOf(healAmt + health));
            		OrderUtils.appendOrderResult(c, String.format("{0} physically healed for {1} health points.", c.getName(), Integer.valueOf(healAmt)));
            	}
            }
        }
    }

}
