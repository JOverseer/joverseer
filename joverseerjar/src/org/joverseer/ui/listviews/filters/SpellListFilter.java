package org.joverseer.ui.listviews.filters;

import java.util.ArrayList;

import org.joverseer.game.Game;
import org.joverseer.metadata.domain.SpellInfo;
import org.joverseer.support.GameHolder;
import org.joverseer.ui.listviews.AbstractListViewFilter;

/**
 * Filter that filters spells based on the spell list
 * 
 * Items must be SpellInfo items
 * 
 * @author Marios Skounakis
 */
public class SpellListFilter extends AbstractListViewFilter {
    String spellList;
    
    public SpellListFilter(String description, String spellList) {
        super(description);
        this.spellList = spellList;
    }

    @Override
	public boolean accept(Object obj) {
        SpellInfo si = (SpellInfo)obj;
        return (this.spellList == null || si.getList().equals(this.spellList));
    }
 
    public static AbstractListViewFilter[] createNationFilters() {
        ArrayList<AbstractListViewFilter> ret = new ArrayList<AbstractListViewFilter>();
        ret.add(new SpellListFilter("All", null));
        Game g = GameHolder.instance().getGame();
        if (!Game.isInitialized(g)) return (AbstractListViewFilter[])ret.toArray(new AbstractListViewFilter[]{});
        ArrayList<String> parsedSpellLists = new ArrayList<String>();
        for (SpellInfo n : (ArrayList<SpellInfo>)g.getMetadata().getSpells().getItems()) {
            String sl = n.getList();
            if (parsedSpellLists.contains(sl)) continue;
            parsedSpellLists.add(sl);
            ret.add(new SpellListFilter(sl, sl));
        }
        return (AbstractListViewFilter[])ret.toArray(new AbstractListViewFilter[]{});
    }

}
