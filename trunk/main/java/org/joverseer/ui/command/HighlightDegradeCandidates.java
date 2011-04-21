package org.joverseer.ui.command;

import org.joverseer.domain.PopulationCenter;
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
 * Highlight all hexes which contain friendly pops that are in risk of degrading
 * (loyalty <= 16)
 * 
 * @author Marios Skounakis
 */
public class HighlightDegradeCandidates extends ActionCommand {
	int loyaltyThreshold = 17;

	public HighlightDegradeCandidates() {
		super("highlightDegradeCandidatesCommand");
	}

	@Override
	protected void doExecuteCommand() {
		if (!ActiveGameChecker.checkActiveGameExists())
			return;

		HighlightHexesMapItem hhmi = new HighlightHexesMapItem();
		Game game = ((GameHolder) Application.instance().getApplicationContext().getBean("gameHolder")).getGame();
		Container<PopulationCenter> pcs = game.getTurn().getPopulationCenters();
		for (PopulationCenter pc : pcs.getItems()) {
			if (pc.getLoyalty() > 0 && pc.getLoyalty() < getLoyaltyThreshold()) {
				hhmi.addHex(pc.getHexNo());
			}
		}
		AbstractMapItem.add(hhmi);
		Application.instance().getApplicationContext().publishEvent(new JOverseerEvent(LifecycleEventsEnum.RefreshMapItems.toString(), hhmi, this));
	}

	public int getLoyaltyThreshold() {
		return loyaltyThreshold;
	}

	public void setLoyaltyThreshold(int loyaltyThreshold) {
		this.loyaltyThreshold = loyaltyThreshold;
	}

}
