package org.joverseer.support;

import org.joverseer.joApplication;
import org.joverseer.game.Game;

/**
 * Simple wrapper for the Game object.
 * 
 * @author Marios Skounakis
 */
public class GameHolder {
    Game game;
    String file;

    public Game getGame() {
        return this.game;
    }

    public void setGame(Game game) {
        this.game = game;
    }
    
    

    public String getFile() {
		return this.file;
	}

	public void setFile(String file) {
		this.file = file;
	}

	public static boolean hasInitializedGame() {
        GameHolder gh = instance();
        return Game.isInitialized(gh.getGame());
    }
    
    public static GameHolder instance() {
        return (GameHolder) joApplication.getApplicationContext().getBean("gameHolder");
    }
}
