package org.joverseer.ui.domain.mapItems;

import java.io.Serializable;

import org.joverseer.game.Game;
import org.joverseer.game.Turn;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.support.Container;
import org.joverseer.support.GameHolder;
import org.springframework.richclient.application.*;

public abstract class AbstractMapItem implements Serializable {
    public abstract String getDescription();
    
    public static void add(AbstractMapItem mapItem) {
        Game g = ((GameHolder)Application.instance().getApplicationContext().getBean("gameHolder")).getGame();
        if (g == null || !Game.isInitialized(g)) return;
        Turn t = g.getTurn();
        if (t == null) return;
        Container mapItems = t.getContainer(TurnElementsEnum.MapItem);
        mapItems.addItem(mapItem);
    }
}
