package org.joverseer.ui.listviews;

import java.util.ArrayList;

import org.joverseer.domain.Character;
import org.joverseer.game.Game;
import org.joverseer.game.Turn;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.support.GameHolder;
import org.joverseer.ui.domain.CharacterInfoWrapper;


public class CharacterInfoListView extends BaseItemListView {

    public CharacterInfoListView(Class tableModelClass) {
        super(tableModelClass);
        // TODO Auto-generated constructor stub
    }

    protected int[] columnWidths() {
        // TODO Auto-generated method stub
        return null;
    }

    protected void setItems() {
        Game g = GameHolder.instance().getGame();
        
        ArrayList<CharacterInfoWrapper> items = new ArrayList<CharacterInfoWrapper>();
        for (int i=0; i<=g.getCurrentTurn(); i++) {
            Turn t = g.getTurn(i);
            if (t == null) continue;
            // get from characters
            for (Character c : (ArrayList<Character>)t.getContainer(TurnElementsEnum.Character).getItems()) {
                
            }
        }
    }
    
}
