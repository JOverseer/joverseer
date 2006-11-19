package org.joverseer.support;

import org.joverseer.game.Game;
import org.springframework.richclient.application.Application;

/**
 * Created by IntelliJ IDEA.
 * User: mskounak
 * Date: Sep 17, 2006
 * Time: 10:56:55 PM
 * To change this template use File | Settings | File Templates.
 */
public class GameHolder {
    Game game;

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public static boolean hasInitializedGame() {
        GameHolder gh = (GameHolder) Application.instance().getApplicationContext().getBean("gameHolder");
        return Game.isInitialized(gh.getGame());
    }
}
