package org.joverseer.ui.command;

import org.joverseer.joApplication;
import org.joverseer.domain.Combat;
import org.joverseer.game.Game;
import org.joverseer.support.Container;
import org.joverseer.ui.LifecycleEventsEnum;
import org.joverseer.ui.domain.mapItems.AbstractMapItem;
import org.joverseer.ui.domain.mapItems.HighlightHexesMapItem;
import org.joverseer.ui.support.ActiveGameChecker;
import org.springframework.richclient.command.ActionCommand;

/**
 * Highlight all hexes where a combat was conducted this turn
 * 
 * @author Marios Skounakis
 */
public class HighlightCombats extends ActionCommand {
	public HighlightCombats() {
		super("highlightCombatsCommand");
	}

	@Override
	protected void doExecuteCommand() {
		if (!ActiveGameChecker.checkActiveGameExists())
			return;

		HighlightHexesMapItem hhmi = new HighlightHexesMapItem();
		Game game = joApplication.getGame();
		Container<Combat> combats = game.getTurn().getCombats();
		for (Combat c : combats.getItems()) {
			hhmi.addHex(c.getHexNo());
		}
		AbstractMapItem.add(hhmi);
		joApplication.publishEvent(LifecycleEventsEnum.RefreshMapItems, hhmi, this);
	}
}
