package org.joverseer.ui.command;

import org.joverseer.domain.Character;
import org.joverseer.game.Game;
import org.joverseer.support.Container;
import org.joverseer.support.GameHolder;
import org.joverseer.support.info.InfoUtils;
import org.joverseer.ui.LifecycleEventsEnum;
import org.joverseer.ui.domain.mapItems.AbstractMapItem;
import org.joverseer.ui.domain.mapItems.HighlightHexesMapItem;
import org.joverseer.ui.support.ActiveGameChecker;
import org.joverseer.ui.support.JOverseerEvent;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.command.ActionCommand;

/**
 * Highlight hexes that contain dragons
 * 
 * @author Marios Skounakis
 */
public class HighlightDragonsCommand extends ActionCommand {

	public HighlightDragonsCommand() {
		super("highlightDragonsCommand");
	}

	@Override
	protected void doExecuteCommand() {
		if (!ActiveGameChecker.checkActiveGameExists())
			return;

		HighlightHexesMapItem hhmi = new HighlightHexesMapItem();
		Game game = ((GameHolder) Application.instance().getApplicationContext().getBean("gameHolder")).getGame();
		Container<Character> chars = game.getTurn().getCharacters();
		for (Character c : chars.getItems()) {
			if (InfoUtils.isDragon(c.getName())) {
				hhmi.addHex(c.getHexNo());
			}
		}
		AbstractMapItem.add(hhmi);
		Application.instance().getApplicationContext().publishEvent(new JOverseerEvent(LifecycleEventsEnum.RefreshMapItems.toString(), hhmi, this));
	}

}
