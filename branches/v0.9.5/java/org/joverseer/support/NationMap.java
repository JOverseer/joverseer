package org.joverseer.support;

import org.joverseer.game.Game;
import org.joverseer.metadata.GameMetadata;
import org.joverseer.metadata.domain.Nation;
import org.springframework.richclient.application.Application;


public class NationMap {
    public static Nation getNationFromNo(int nationNo) {
        Game g = ((GameHolder)Application.instance().getApplicationContext().getBean("gameHolder")).getGame();
        if (Game.isInitialized(g)) {
            GameMetadata gm = g.getMetadata();
            return gm.getNationByNum(nationNo);
        }
        return null;
    }

}
