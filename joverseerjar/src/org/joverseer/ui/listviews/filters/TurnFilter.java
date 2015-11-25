package org.joverseer.ui.listviews.filters;

import org.joverseer.domain.IHasTurnNumber;
import org.joverseer.support.GameHolder;
import org.joverseer.ui.listviews.AbstractListViewFilter;

/**
 * Turn filter Items must implement the IHasTurnNumber interface
 * 
 * @author Marios Skounakis
 */
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

    @Override
	public boolean accept(Object obj) {
        if (!IHasTurnNumber.class.isInstance(obj))
            return false;
        if (this.turnNo == -1)
            return true;
        IHasTurnNumber t = (IHasTurnNumber) obj;
        if (this.match == EXACT) {
            return t.getTurnNo() == this.turnNo;
        } else if (this.match == BEFORE) {
            return t.getTurnNo() <= this.turnNo;
        } else {
            return t.getTurnNo() >= this.turnNo;
        }
    }

    /**
     * Creates the standard filters: - Current turn - All turns No specific turns are included
     */
    public static TurnFilter[] createTurnFiltersCurrentTurnAndAllTurns() {
        if (!GameHolder.hasInitializedGame())
            return new TurnFilter[] {};
        return new TurnFilter[] {
                new TurnFilter("Current turn", GameHolder.instance().getGame().getCurrentTurn(), EXACT),
                new TurnFilter("All turns", -1, EXACT)};
    }
}
