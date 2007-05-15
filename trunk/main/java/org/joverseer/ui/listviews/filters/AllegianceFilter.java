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
    boolean inverse = false;
    
    public AllegianceFilter(String description, NationAllegianceEnum allegiance) {
        super(description);
        this.allegiance = allegiance;
    }
    
    

    public AllegianceFilter(String description, NationAllegianceEnum allegiance, boolean inverse) {
        super(description);
        this.allegiance = allegiance;
        this.inverse = inverse;
    }

    public boolean accept(Object obj) {
        IBelongsToNation o = (IBelongsToNation)obj;
        Game g = GameHolder.instance().getGame();
        NationRelations nr = (NationRelations)g.getTurn().getContainer(TurnElementsEnum.NationRelation).findFirstByProperty("nationNo", o.getNationNo());
        boolean ret;
        if (nr == null) {
            ret = false;
        } else {
            ret = nr.getAllegiance().equals(allegiance);;
        }
        if  (inverse) {
            ret = !ret;
        }
        return ret;
    }
 
    public static AbstractListViewFilter[] createAllegianceFilters() {
        ArrayList<AbstractListViewFilter> ret = new ArrayList<AbstractListViewFilter>();
        for (NationAllegianceEnum allegiance : NationAllegianceEnum.values()) {
            ret.add(new AllegianceFilter(allegiance.toString(), allegiance));
        }
        for (NationAllegianceEnum allegiance : NationAllegianceEnum.values()) {
            ret.add(new AllegianceFilter("Not " + allegiance.toString(), allegiance, true));
        }
        return (AbstractListViewFilter[])ret.toArray(new AbstractListViewFilter[]{});
    }

}