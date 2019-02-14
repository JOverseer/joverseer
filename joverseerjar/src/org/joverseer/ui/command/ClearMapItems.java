package org.joverseer.ui.command;

import org.joverseer.JOApplication;
import org.joverseer.game.Game;
import org.joverseer.game.Turn;
import org.joverseer.support.Container;
import org.joverseer.ui.LifecycleEventsEnum;
import org.joverseer.ui.domain.mapItems.AbstractMapItem;
import org.springframework.richclient.command.ActionCommand;

/**
 * Clear the map items
 * 
 * @author Marios Skounakis
 */
public class ClearMapItems extends ActionCommand {

	public ClearMapItems() {
		super("clearMapItemsCommand");
	}

	@Override
	protected void doExecuteCommand() {
		Game g = JOApplication.getGame();
		if (g == null || !Game.isInitialized(g))
			return;
		Turn t = g.getTurn();
		if (t == null)
			return;
		Container<AbstractMapItem> mapItems = t.getMapItems();
		mapItems.removeAll(mapItems.getItems());
		JOApplication.publishEvent(LifecycleEventsEnum.RefreshMapItems, this, this);

	}
}
