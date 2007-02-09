package org.joverseer.tools;

import java.util.Comparator;
import org.joverseer.domain.Character;
import org.joverseer.domain.CharacterDeathReasonEnum;
import org.joverseer.domain.NationRelations;
import org.joverseer.game.Game;
import org.joverseer.game.Turn;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.metadata.domain.Nation;
import org.joverseer.metadata.domain.NationAllegianceEnum;
import org.joverseer.support.GameHolder;


public class CharacterDeathAllegianceNameComparator implements Comparator {

    public int compare(Object o1, Object o2) {
        Character c1 = (Character)o1;
        Character c2 = (Character)o2;
        int i;
        i = compareDeath(c1, c2);
        if (i != 0) return i;
        i = compareAllegiance(c1, c2);
        if (i != 0) return i;
        return c1.getName().compareTo(c2.getName());
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
        
    private int compareAllegiance(Character c1, Character c2) {
        
        Game g = GameHolder.instance().getGame();
        if (!Game.isInitialized(g)) return 0;
        Turn t = g.getTurn();
        if (t == null) return 0;
        NationRelations nr1 = (NationRelations)t.getContainer(TurnElementsEnum.NationRelation).findFirstByProperty("nationNo", c1.getNationNo());
        NationRelations nr2 = (NationRelations)t.getContainer(TurnElementsEnum.NationRelation).findFirstByProperty("nationNo", c2.getNationNo());
        NationRelations nr = (NationRelations)t.getContainer(TurnElementsEnum.NationRelation).findFirstByProperty("nationNo", g.getMetadata().getNationNo());
        if (nr1 == null) return 1;
        if (nr2 == null) return -1;
        if (nr1.getAllegiance() == nr2.getAllegiance()) return 0;
        if (nr1.getAllegiance().equals(NationAllegianceEnum.Neutral)) return 1;
        if (nr1.getAllegiance() == nr.getAllegiance()) return -1;
        return 1;
    }
}
