package org.joverseer.ui.command;

import org.joverseer.domain.Combat;
import org.joverseer.game.Game;
import org.joverseer.support.Container;
import org.joverseer.support.GameHolder;
import org.joverseer.ui.LifecycleEventsEnum;
import org.joverseer.ui.domain.mapItems.AbstractMapItem;
import org.joverseer.ui.domain.mapItems.HighlightHexesMapItem;
import org.joverseer.ui.support.ActiveGameChecker;
import org.joverseer.ui.support.JOverseerEvent;
import org.springframework.richclient.application.Application;
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
		Game game = ((GameHolder) Application.instance().getApplicationContext().getBean("gameHolder")).getGame();
		Container<Combat> combats = game.getTurn().getCombats();
		for (Combat c : combats.getItems()) {
			hhmi.addHex(c.getHexNo());
		}
		AbstractMapItem.add(hhmi);
		Application.instance().getApplicationContext().publishEvent(new JOverseerEvent(LifecycleEventsEnum.RefreshMapItems.toString(), hhmi, this));
	}
}
