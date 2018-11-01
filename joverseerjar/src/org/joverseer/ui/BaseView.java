package org.joverseer.ui;

import org.joverseer.game.Game;
import org.joverseer.game.Turn;
import org.joverseer.support.GameHolder;
import org.springframework.richclient.application.support.AbstractView;

/**
 * A way to customize AbstractViews in joverseer.
 */
public abstract class BaseView extends AbstractView {

	protected GameHolder gameHolder=null;
	protected Game game=null;

	public GameHolder getGameHolder() {
		if (this.gameHolder == null) {
			this.gameHolder = GameHolder.instance();
		}
		return this.gameHolder;
	}

	public void setGameHolder(GameHolder gameHolder) {
		this.gameHolder = gameHolder;
	}

	protected Game getGame() {
		if (this.game == null ) {
			this.game = GameHolder.instance().getGame();
		}
		return this.game;
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
}
