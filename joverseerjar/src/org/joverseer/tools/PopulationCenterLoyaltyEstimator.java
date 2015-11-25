package org.joverseer.tools;

import org.joverseer.domain.InfoSourceValue;
import org.joverseer.domain.PopulationCenter;
import org.joverseer.game.Game;
import org.joverseer.game.Turn;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.support.GameHolder;

/**
 * Utility class that finds the latest applicable loyalty estimate for a given pop center,
 * or null if no such estimate can be found.
 * 
 * More specifically it:
 * - starts from the current turn
 * - goes back one turn at a time, checking for an existing estimate
 * - if at any time the pop center's size or nation is changed, it returns null, 
 * because older estimates are going to be not applicable
 * 
 * @author Marios Skounakis
 */
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
