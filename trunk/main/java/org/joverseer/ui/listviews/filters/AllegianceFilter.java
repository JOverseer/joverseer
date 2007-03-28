package org.joverseer.ui.listviews.filters;

import java.util.ArrayList;

import org.joverseer.domain.IBelongsToNation;
import org.joverseer.domain.NationRelations;
import org.joverseer.game.Game;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.metadata.domain.NationAllegianceEnum;
import org.joverseer.support.GameHolder;
import org.joverseer.ui.listviews.AbstractListViewFilter;


public class AllegianceFilter extends AbstractListViewFilter {
    NationAllegianceEnum allegiance;
    
    public AllegianceFilter(String description, NationAllegianceEnum allegiance) {
        super(description);
        this.allegiance = allegiance;
    }

    public boolean accept(Object obj) {
        IBelongsToNation o = (IBelongsToNation)obj;
        Game g = GameHolder.instance().getGame();
        NationRelations nr = (NationRelations)g.getTurn().getContainer(TurnElementsEnum.NationRelation).findFirstByProperty("nationNo", o.getNationNo());
        if (nr == null) return false;
        return nr.getAllegiance().equals(allegiance);
    }
 
    public static AbstractListViewFilter[] createAllegianceFilters() {
        ArrayList<AbstractListViewFilter> ret = new ArrayList<AbstractListViewFilter>();
        for (NationAllegianceEnum allegiance : NationAllegianceEnum.values()) {
            ret.add(new AllegianceFilter(allegiance.toString(), allegiance));
        }
        return (AbstractListViewFilter[])ret.toArray(new AbstractListViewFilter[]{});
    }

}
