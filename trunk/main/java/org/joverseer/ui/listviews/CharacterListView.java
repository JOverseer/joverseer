package org.joverseer.ui.listviews;

import java.util.ArrayList;

import org.joverseer.game.Game;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.metadata.domain.Nation;
import org.joverseer.support.GameHolder;
import org.joverseer.domain.Character;


public class CharacterListView extends ItemListView {

    protected AbstractListViewFilter[] getFilters() {
        ArrayList<AbstractListViewFilter> ret = new ArrayList<AbstractListViewFilter>();
        ret.add(new CharacterNationFilter("All", -1));
        Game g = GameHolder.instance().getGame();
        if (!Game.isInitialized(g)) return (AbstractListViewFilter[])ret.toArray(new AbstractListViewFilter[]{});
        for (Nation n : (ArrayList<Nation>)g.getMetadata().getNations()) {
            ret.add(new CharacterNationFilter(n.getName(), n.getNumber()));
        }
        return (AbstractListViewFilter[])ret.toArray(new AbstractListViewFilter[]{});
    }

    public CharacterListView() {
        super(TurnElementsEnum.Character, CharacterTableModel.class);
    }

    protected int[] columnWidths() {
        return new int[]{40, 120,
                        32, 32, 32, 32,
                        32, 32, 32, 32,
                        32, 32, 32, 32, 32};
    }
    
    
    
    public class CharacterNationFilter extends AbstractListViewFilter {
        int nationNo;
        
        public CharacterNationFilter(String description, int nationNo) {
            super(description);
            this.nationNo = nationNo;
        }

        public boolean accept(Object obj) {
            Character ch = (Character)obj;
            return (nationNo == -1 || ch.getNationNo() == nationNo);
        }
        
    }

}
