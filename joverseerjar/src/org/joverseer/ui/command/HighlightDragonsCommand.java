package org.joverseer.ui.command;

import org.joverseer.JOApplication;
import org.joverseer.domain.Character;
import org.joverseer.game.Game;
import org.joverseer.support.Container;
import org.joverseer.support.info.InfoUtils;
import org.joverseer.ui.LifecycleEventsEnum;
import org.joverseer.ui.domain.mapItems.AbstractMapItem;
import org.joverseer.ui.domain.mapItems.HighlightHexesMapItem;
import org.joverseer.ui.support.ActiveGameChecker;
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
		Game game = JOApplication.getGame();
		Container<Character> chars = game.getTurn().getCharacters();
		for (Character c : chars.getItems()) {
			if (InfoUtils.isDragon(c.getName())) {
				hhmi.addHex(c.getHexNo());
			}
		}
		AbstractMapItem.add(hhmi);
		JOApplication.publishEvent(LifecycleEventsEnum.RefreshMapItems, hhmi, this);
	}

}
