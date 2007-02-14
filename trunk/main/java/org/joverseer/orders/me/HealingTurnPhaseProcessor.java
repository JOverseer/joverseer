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
        return healthRecoveryPerTurn;
    }
    
    public void setHealthRecoveryPerTurn(int healthRecoveryPerTurn) {
        this.healthRecoveryPerTurn = healthRecoveryPerTurn;
    }

    public void processPhase(Turn t) {
        for (Character c : (ArrayList<Character>)t.getContainer(TurnElementsEnum.Character).getItems()) {
            if (c.getHealth() != null && c.getHealth() > 0 && c.getHealth() < 100) {
                
                int healAmt = Math.min(100 - c.getHealth(), getHealthRecoveryPerTurn());
                c.setHealth(healAmt + c.getHealth());
                OrderUtils.appendOrderResult(c, String.format("{0} physically healed for {1} health points.", c.getName(), healAmt));
            }
        }
    }
    
}
