package org.joverseer.ui.command;

import org.joverseer.domain.Artifact;
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
		Game game = ((GameHolder) Application.instance().getApplicationContext().getBean("gameHolder")).getGame();
		Container<Artifact> artifacts = game.getTurn().getArtifacts();
		for (Artifact arti : artifacts.getItems()) {
			hhmi.addHex(arti.getHexNo());
		}
		AbstractMapItem.add(hhmi);
		Application.instance().getApplicationContext().publishEvent(new JOverseerEvent(LifecycleEventsEnum.RefreshMapItems.toString(), hhmi, this));
	}

}
