package org.joverseer.ui.listviews.filters;

import java.util.ArrayList;

import org.joverseer.domain.IBelongsToNation;
import org.joverseer.game.Game;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.metadata.domain.Nation;
import org.joverseer.support.GameHolder;
import org.joverseer.ui.listviews.AbstractListViewFilter;

public class NationFilter extends AbstractListViewFilter {
    static int ALL_NATIONS = -1;
    static int ALL_IMPORTED = -2;
    
    int nationNo;
    
    public NationFilter(String description, int nationNo) {
        super(description);
        this.nationNo = nationNo;
    }

    public boolean accept(Object obj) {
        IBelongsToNation o = (IBelongsToNation)obj;
        if (nationNo == ALL_NATIONS) return true;
        if (nationNo == ALL_IMPORTED) {
            if (!GameHolder.instance().hasInitializedGame()) return true;
            return GameHolder.instance().getGame().getTurn().getContainer(TurnElementsEnum.PlayerInfo).findFirstByProperty("nationNo", o.getNationNo()) != null; 
        }
        return o.getNationNo() != null && o.getNationNo() == nationNo;
    }
    
    public static AbstractListViewFilter[] createNationFilters() {
        return createNationFilters(false);
    }
 
    public static AbstractListViewFilter[] createNationFilters(boolean includeAllImported) {
        ArrayList<AbstractListViewFilter> ret = new ArrayList<AbstractListViewFilter>();
        if (includeAllImported) {
            ret.add(new NationFilter("All imported", ALL_IMPORTED));
        }
        ret.add(new NationFilter("All", ALL_NATIONS));
        Game g = GameHolder.instance().getGame();
        if (!Game.isInitialized(g)) return (AbstractListViewFilter[])ret.toArray(new AbstractListViewFilter[]{});
        for (Nation n : (ArrayList<Nation>)g.getMetadata().getNations()) {
            ret.add(new NationFilter(n.getName(), n.getNumber()));
        }
        return (AbstractListViewFilter[])ret.toArray(new AbstractListViewFilter[]{});
    }
    
    public static AbstractListViewFilter[] createAllAndAllImportedNationFilters() {
        ArrayList<AbstractListViewFilter> ret = new ArrayList<AbstractListViewFilter>();
        ret.add(new NationFilter("All imported", ALL_IMPORTED));
        ret.add(new NationFilter("All", ALL_NATIONS));
        return (AbstractListViewFilter[])ret.toArray(new AbstractListViewFilter[]{});
    }
}
