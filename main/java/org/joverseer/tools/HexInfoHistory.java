package org.joverseer.tools;

import org.joverseer.domain.HexInfo;
import org.joverseer.game.Game;
import org.joverseer.game.Turn;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.support.GameHolder;

/**
 * Utility class that computes the last turn that a hex was visible
 * 
 * @author Marios Skounakis
 */
//TODO is this really needed as a separate class?
public class HexInfoHistory {
    public static Integer getLatestHexInfoTurnNoForHex(int hexNo) {
        if (!GameHolder.instance().hasInitializedGame()) return null;
        Game game = GameHolder.instance().getGame();
        for (int i = game.getCurrentTurn(); i>=0; i--) {
            Turn t = game.getTurn(i);
            if (t == null) continue;
            HexInfo hi = (HexInfo)t.getContainer(TurnElementsEnum.HexInfo).findFirstByProperty("hexNo", hexNo);
            if (hi.getVisible()) return i;
        }
        return -1;
    }
}
