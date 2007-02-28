package org.joverseer.orders.me;

import java.util.ArrayList;

import org.joverseer.domain.FortificationSizeEnum;
import org.joverseer.domain.NationEconomy;
import org.joverseer.domain.PopulationCenter;
import org.joverseer.domain.PopulationCenterSizeEnum;
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
                int newLoyalty = pc.getLoyalty() + getLoyaltyChange(taxRate);
                pc.setLoyalty(newLoyalty);
                if (pc.getLoyalty() < 15 && 
                		pc.getFortification().equals(FortificationSizeEnum.none) &&
                		t.getContainer(TurnElementsEnum.Character).findAllByProperties(new String[]{"hexNo", "nationNo"}, new Object[]{pc.getHexNo(), pc.getNationNo()}).size() == 0) {
                	// chance PC will degrade
                	if (OrderUtils.getRandomNumber(100) <= 10) {
                		// PC degrades
                		
                		PopulationCenterSizeEnum newSize = null;
                		if (pc.getSize() == PopulationCenterSizeEnum.camp) {
                			newSize = null;
                			// remove PC completely
                		} else if (pc.getSize() == PopulationCenterSizeEnum.village) {
                			newSize = PopulationCenterSizeEnum.camp;
                		} else if (pc.getSize() == PopulationCenterSizeEnum.town) {
                			newSize = PopulationCenterSizeEnum.village;
                		} else if (pc.getSize() == PopulationCenterSizeEnum.majorTown) {
                			newSize = PopulationCenterSizeEnum.town;
                		} else if (pc.getSize() == PopulationCenterSizeEnum.city) {
                			newSize = PopulationCenterSizeEnum.majorTown;
                		}
                		if (newSize == null) {
                			t.getContainer(TurnElementsEnum.PopulationCenter).removeItem(pc);
                		} else {
                			pc.setSize(newSize);
                		}
                	}
                }
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
