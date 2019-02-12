package org.joverseer.support;

import org.joverseer.joApplication;
import org.joverseer.game.Game;
import org.joverseer.game.Turn;
import org.springframework.context.ApplicationContext;

/**
 * Simple wrapper for the Game object.
 *
 * @author Marios Skounakis
 *  there's only ever one gameHolder, so spring can create it and inject it for everyone who wants it.
 *  but the properties can change during the application's lifetime, so every instance that has it injected needs to be notified of a game change.
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

	public boolean isGameInitialized() {
        return Game.isInitialized(this.getGame());
	}

	public static boolean hasInitializedGame() {
        return instance().isGameInitialized();
    }
	/**
	 * should be deprecated. Use instance(ApplicationContext context).
	 * better for testing.
	 * @return
	 */
    public static GameHolder instance() {
        return GameHolder.instance(joApplication.getApplicationContext());
    }
    /**
     * encapsulate how to get the gameHolder from the ApplicationContext.
     * @param context
     * @return
     */
    public static GameHolder instance(ApplicationContext context) {
    	return (GameHolder) context.getBean("gameHolder");
    }
    public static Turn getTurnOrNull(GameHolder gh) {
    	if (gh == null) {
    		return null;
    	}
    	if (gh.game == null) {
    		return null;
    	}
    	return Game.getTurnOrNull(gh.game);
    }
}
