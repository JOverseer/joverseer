package org.joverseer.tools;

import org.joverseer.domain.InfoSourceValue;
import org.joverseer.domain.PopulationCenter;
import org.joverseer.game.Game;
import org.joverseer.game.Turn;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.support.GameHolder;


public class PopulationCenterLoyaltyEstimator {
    public static InfoSourceValue getLoyaltyEstimateForPopCenter(PopulationCenter pc) {
        if (pc.getLoyaltyEstimate() != null) return pc.getLoyaltyEstimate();
        Game g = GameHolder.instance().getGame();
        for (int i=g.getCurrentTurn() - 1; i>=0; i--) {
            Turn t = g.getTurn(i);
            if (t == null) continue;
            PopulationCenter newpc = (PopulationCenter)t.getContainer(TurnElementsEnum.PopulationCenter).findFirstByProperty("hexNo", pc.getHexNo());
            if (newpc == null) return null;
            // check if pc has changed in any significant way
            if (newpc.getSize() != pc.getSize()) return null;
            if (newpc.getNationNo() != pc.getNationNo()) return null;
            
            if (newpc.getLoyaltyEstimate() != null) return newpc.getLoyaltyEstimate();
        }
        return null;
    }
}
