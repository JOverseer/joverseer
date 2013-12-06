package org.joverseer.orders.me;

import java.util.ArrayList;

import org.joverseer.game.Turn;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.orders.AbstractTurnPhaseProcessor;
import org.joverseer.orders.OrderUtils;
import org.joverseer.domain.Character;

public class HealingTurnPhaseProcessor extends AbstractTurnPhaseProcessor {
    
    public HealingTurnPhaseProcessor(String name) {
        super(name);
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
            		c.setHealth(new Integer(healAmt + health));
            		OrderUtils.appendOrderResult(c, String.format("{0} physically healed for {1} health points.", c.getName(), new Integer(healAmt)));
            	}
            }
        }
    }
    
}
