package org.joverseer.tools;

import org.joverseer.domain.Character;
import org.joverseer.domain.HexInfo;
import org.joverseer.domain.InformationSourceEnum;
import org.joverseer.game.Game;
import org.joverseer.game.Turn;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.support.GameHolder;

/**
 * Utility class that computes the last turn that a hex was visible
 * 
 * @author Marios Skounakis
 */
// TODO is this really needed as a separate class?
public class HexInfoHistory {
	public static Integer getLatestHexInfoTurnNoForHex(int hexNo) {
		GameHolder.instance();
		if (!GameHolder.hasInitializedGame())
			return null;
		Game game = GameHolder.instance().getGame();
		for (int i = game.getCurrentTurn(); i >= 0; i--) {
			Turn t = game.getTurn(i);
			if (t == null)
				continue;
			HexInfo hi = (HexInfo) t.getContainer(TurnElementsEnum.HexInfo).findFirstByProperty("hexNo", hexNo);
			if (hi.getVisible())
				return i;
			// check friendly char present
			for (Character c : t.getCharactersAtHex(hexNo)) {
				if (c.getInformationSource() != null && c.getInformationSource().equals(InformationSourceEnum.exhaustive) && !c.isDead()) {
					return i;
				}
			}
		}
		return -1;
	}
}
