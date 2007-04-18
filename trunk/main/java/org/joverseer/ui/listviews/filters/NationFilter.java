package org.joverseer.ui.listviews.filters;

import java.util.ArrayList;

import org.joverseer.domain.IBelongsToNation;
import org.joverseer.game.Game;
import org.joverseer.metadata.domain.Nation;
import org.joverseer.support.GameHolder;
import org.joverseer.ui.listviews.AbstractListViewFilter;

public class NationFilter extends AbstractListViewFilter {
    int nationNo;
    
    public NationFilter(String description, int nationNo) {
        super(description);
        this.nationNo = nationNo;
    }

    public boolean accept(Object obj) {
        IBelongsToNation o = (IBelongsToNation)obj;
        return (nationNo == -1 || (o.getNationNo() != null && o.getNationNo() == nationNo));
    }
 
    public static AbstractListViewFilter[] createNationFilters() {
        ArrayList<AbstractListViewFilter> ret = new ArrayList<AbstractListViewFilter>();
        ret.add(new NationFilter("All", -1));
        Game g = GameHolder.instance().getGame();
        if (!Game.isInitialized(g)) return (AbstractListViewFilter[])ret.toArray(new AbstractListViewFilter[]{});
        for (Nation n : (ArrayList<Nation>)g.getMetadata().getNations()) {
            ret.add(new NationFilter(n.getName(), n.getNumber()));
        }
        return (AbstractListViewFilter[])ret.toArray(new AbstractListViewFilter[]{});
    }
}
