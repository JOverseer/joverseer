package org.joverseer.support;

import org.joverseer.game.Game;
import org.springframework.richclient.application.Application;


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
    
    public static GameHolder instance() {
        return (GameHolder) Application.instance().getApplicationContext().getBean("gameHolder");
    }
}
