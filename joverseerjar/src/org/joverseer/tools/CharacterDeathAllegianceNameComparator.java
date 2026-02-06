package org.joverseer.tools;

import java.util.Comparator;

import org.joverseer.domain.Character;
import org.joverseer.domain.CharacterDeathReasonEnum;
import org.joverseer.domain.NationRelations;
import org.joverseer.game.Game;
import org.joverseer.game.Turn;
import org.joverseer.metadata.domain.NationAllegianceEnum;
import org.joverseer.preferences.PreferenceRegistry;
import org.joverseer.support.GameHolder;

/**
 * Comparator that compares armies based on: - their death reason (not dead
 * chars go first) - their allegiance - their nation (if preference
 * currentHexView.sortCharacters is allegianceNationName) - their name in order
 * of precedence
 * 
 * @author Marios Skounakis
 */
public class CharacterDeathAllegianceNameComparator implements Comparator<Character> {

	@Override
	public int compare(Character c1, Character c2) {
		int i;
		i = compareDeath(c1, c2);
		if (i != 0) {
			return i;
		}
		i = compareAllegiance(c1, c2);
		if (i != 0) {
			return i;
		}
		
		String pval = PreferenceRegistry.instance().getPreferenceValue("currentHexView.sortCharacters");
		if (pval != null && pval.equals("allegianceNationName")) {
			i = compareNation(c1, c2);
			if (i != 0)
				return i;
		}
		return c1.getId().compareTo(c2.getId());
	}

	private int compareDeath(Character c1, Character c2) {
		boolean c1Dead = c1.getDeathReason() == CharacterDeathReasonEnum.NotDead;
		boolean c2Dead = c2.getDeathReason() == CharacterDeathReasonEnum.NotDead;
		if (c1Dead == c2Dead) {
			return 0;
		}
		if (c1Dead) {
			return -1;
		}
		return 1;
	}

	private int compareNation(Character c1, Character c2) {
		if (c1.getNationNo() == null || c1.getNationNo() == 0) {
			return 1;
		}
		if (c2.getNationNo() == null || c2.getNationNo() == 0) {
			return -1;
		}
		return c1.getNationNo() - c2.getNationNo();
	}

	private int compareAllegiance(Character c1, Character c2) {

		Game g = GameHolder.instance().getGame();
		if (!Game.isInitialized(g))
			return 0;
		Turn t = g.getTurn();
		if (t == null)
			return 0;
		
		NationRelations nr1 = t.getNationRelations(c1.getNationNo());
		NationRelations nr2 = t.getNationRelations(c2.getNationNo());
		NationRelations nr = t.getNationRelations(g.getMetadata().getNationNo());
		if (nr1 == null)
			return 1;
		if (nr2 == null)
			return -1;
		if (nr1.getAllegiance() == nr2.getAllegiance())
			return 0;
		if (nr1.getAllegiance() == nr.getAllegiance())
			return -1;
		if (nr1.getAllegiance().equals(NationAllegianceEnum.Neutral))
			return 1;
		return 1;
	}
}
