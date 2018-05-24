package org.joverseer.ui.command;

import org.joverseer.joApplication;
import org.joverseer.domain.Artifact;
import org.joverseer.game.Game;
import org.joverseer.support.Container;
import org.joverseer.ui.LifecycleEventsEnum;
import org.joverseer.ui.domain.mapItems.AbstractMapItem;
import org.joverseer.ui.domain.mapItems.HighlightHexesMapItem;
import org.joverseer.ui.support.ActiveGameChecker;
import org.springframework.richclient.command.ActionCommand;

/**
 * Highlights hexes that contain LA/LAT'ed artifacts
 * 
 * @author Marios Skounakis
 */
public class HighlightLocatedArtifacts extends ActionCommand {
	public HighlightLocatedArtifacts() {
		super("highlightLocatedArtifactsCommand");
	}

	@Override
	protected void doExecuteCommand() {
		if (!ActiveGameChecker.checkActiveGameExists())
			return;

		HighlightHexesMapItem hhmi = new HighlightHexesMapItem();
		Game game = joApplication.getGame();
		Container<Artifact> artifacts = game.getTurn().getArtifacts();
		for (Artifact arti : artifacts.getItems()) {
			hhmi.addHex(arti.getHexNo());
		}
		AbstractMapItem.add(hhmi);
		joApplication.publishEvent(LifecycleEventsEnum.RefreshMapItems, hhmi, this);
	}

}
