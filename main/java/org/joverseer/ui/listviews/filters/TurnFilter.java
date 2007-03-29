package org.joverseer.ui.listviews.filters;

import org.joverseer.domain.IHasTurnNumber;
import org.joverseer.support.GameHolder;
import org.joverseer.ui.listviews.AbstractListViewFilter;


public class TurnFilter extends AbstractListViewFilter {
    public static int EXACT = 1;
    public static int BEFORE = 2;
    public static int AFTER = 3;
    int turnNo;
    int match;

    private TurnFilter(String description, int turnNo, int match) {
        super(description);
        this.turnNo = turnNo;
        this.match = match;
    }

    public boolean accept(Object obj) {
        if (!IHasTurnNumber.class.isInstance(obj)) return false;
        if (turnNo == -1) return true;
        IHasTurnNumber t = (IHasTurnNumber)obj;
        if (match == EXACT) {
            return t.getTurnNo() == turnNo;
        } else if (match == BEFORE) {
            return t.getTurnNo() <= turnNo;
        } else {
            return t.getTurnNo() >= turnNo;
        }
    }
    
    
    public static TurnFilter[] createTurnFiltersCurrentTurnAndAllTurns() {
        if (!GameHolder.hasInitializedGame()) return new TurnFilter[]{};
        return new TurnFilter[]{
                new TurnFilter("Current turn", GameHolder.instance().getGame().getCurrentTurn(), EXACT),
                new TurnFilter("All turns", -1, EXACT)
                };
    }
}