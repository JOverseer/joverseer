package org.joverseer.orders.me;

import java.util.ArrayList;

import org.joverseer.domain.NationEconomy;
import org.joverseer.domain.PopulationCenter;
import org.joverseer.domain.ProductEnum;
import org.joverseer.game.Game;
import org.joverseer.game.Turn;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.orders.AbstractTurnPhaseProcessor;
import org.joverseer.orders.OrderUtils;


public class LoyaltyPhaseProcessor extends AbstractTurnPhaseProcessor {

    public LoyaltyPhaseProcessor(String name) {
        super(name);
    }

    public void processPhase(Turn t) {
        Game g = OrderUtils.getGame();
        for (int i=1; i<=g.getMetadata().getNationNo(); i++) {
            NationEconomy ne = (NationEconomy)t.getContainer(TurnElementsEnum.NationEconomy).findFirstByProperty("nationNo", i);
            int taxRate = ne.getTaxRate();
            for (PopulationCenter pc : (ArrayList<PopulationCenter>)t.getContainer(TurnElementsEnum.PopulationCenter).findAllByProperty("nationNo", i)) {
                
            }
        }
    }
    
    private int getLoyaltyChange(int taxRate) {
        if (taxRate < 40) {
            return OrderUtils.getRandomNumber(0, 4);
        }
        if (taxRate > 60) {
            return OrderUtils.getRandomNumber(-3, 3);
        }
        return OrderUtils.getRandomNumber(-4, 0);
    }

}
