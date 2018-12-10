package org.joverseer.ui;

import org.joverseer.game.Game;
import org.joverseer.game.Turn;
import org.joverseer.support.GameHolder;
import org.springframework.richclient.application.support.AbstractView;

/**
 * A way to customize AbstractViews in joverseer.
 * Note that only writes to the gameHolder and game are synchronised, but reads shouldn't be a problem.
 */
public abstract class BaseView extends AbstractView {

	protected GameHolder gameHolder=null;
	protected Game game=null;

	public GameHolder getGameHolder() {
		if (this.gameHolder == null) {
			setGameHolder2(GameHolder.instance());
		}
		return this.gameHolder;
	}

	private synchronized void setGameHolder2(GameHolder gameHolder) {
		this.gameHolder = gameHolder;
	}
	
	public void setGameHolder(GameHolder gameHolder) {
		this.gameHolder = gameHolder;
	}

	protected Game getGame() {
		if (this.game == null ) {
			setGame2(GameHolder.instance().getGame());
		}
		return this.game;
	}
	private synchronized void setGame2(Game game) {
		this.game = game;
	}
	/**
	 * 
	 * @return null if Game is not initialized.
	 */
	protected Turn getTurn() {
		if (!Game.isInitialized(getGame()))
			return null;
		return this.game.getTurn();
	}
	/**
	 * subclasses MUST call this to reset the gameholder when the game is changed!
	 */
	protected synchronized void resetGame() {
		this.gameHolder = null;
		this.game = null;
		this.getGameHolder();
		this.getGame();
	}
}
