package org.joverseer.tools;

import java.util.Comparator;

import org.joverseer.domain.Army;
import org.joverseer.domain.Character;
import org.joverseer.domain.NationRelations;
import org.joverseer.game.Game;
import org.joverseer.game.Turn;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.metadata.domain.Nation;
import org.joverseer.metadata.domain.NationAllegianceEnum;
import org.joverseer.support.GameHolder;


public class ArmyAllegianceNameComparator implements Comparator {

    public int compare(Object o1, Object o2) {
        Army c1 = (Army)o1;
        Army c2 = (Army)o2;
        int i = compareAllegiance(c1, c2);
        if (i == 0) {
            return c1.getCommanderName().compareTo(c2.getCommanderName());
        }
        return i;
    }
        
    private int compareAllegiance(Army c1, Army c2) {
        
        Game g = GameHolder.instance().getGame();
        if (!Game.isInitialized(g)) return 0;
        Turn t = g.getTurn();
        if (t == null) return 0;
        NationAllegianceEnum nr1 = c1.getNationAllegiance();
        NationAllegianceEnum nr2 = c2.getNationAllegiance();
        NationRelations nr = (NationRelations)t.getContainer(TurnElementsEnum.NationRelation).findFirstByProperty("nationNo", g.getMetadata().getNationNo());
        if (nr1 == null) return 1;
        if (nr2 == null) return -1;
        if (nr1 == nr2) return 0;
        if (nr1.equals(NationAllegianceEnum.Neutral)) return 1;
        if (nr1 == nr.getAllegiance()) return -1;
        return 1;
    }
}
