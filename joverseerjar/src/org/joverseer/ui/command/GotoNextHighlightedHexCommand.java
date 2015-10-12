package org.joverseer.ui.command;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;

import org.joverseer.support.Container;
import org.joverseer.support.GameHolder;
import org.joverseer.ui.LifecycleEventsEnum;
import org.joverseer.ui.domain.mapItems.AbstractMapItem;
import org.joverseer.ui.domain.mapItems.HighlightHexesMapItem;
import org.joverseer.ui.map.MapPanel;
import org.joverseer.ui.support.ActiveGameChecker;
import org.joverseer.ui.support.JOverseerEvent;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.command.ActionCommand;

/**
 * Selects the next highlighted hex based on the current hex selection
 * 
 * @author Marios Skounakis
 */
public class GotoNextHighlightedHexCommand extends ActionCommand {
	public GotoNextHighlightedHexCommand() {
		super("gotoNextHighlightedHexCommand");
	}

	@Override
	protected void doExecuteCommand() {
		if (!ActiveGameChecker.checkActiveGameExists())
			return;

		Container<AbstractMapItem> mapItemsC = GameHolder.instance().getGame().getTurn().getMapItems();
		ArrayList<Integer> hightlightedHexes = new ArrayList<Integer>();
		for (AbstractMapItem mi : mapItemsC.items) {
			if (HighlightHexesMapItem.class.isInstance(mi)) {
				HighlightHexesMapItem hhmi = (HighlightHexesMapItem) mi;
				for (Integer i : hhmi.getHexesToHighlight()) {
					if (!hightlightedHexes.contains(i)) {
						hightlightedHexes.add(i);
					}
				}
			}
		}
		Integer currentSelectedHex = MapPanel.instance().getSelectedHex().x * 100 + MapPanel.instance().getSelectedHex().y;
		hightlightedHexes.add(currentSelectedHex);
		Collections.sort(hightlightedHexes);
		int i = hightlightedHexes.indexOf(currentSelectedHex);
		Integer hexToHighlight = null;
		if (i == -1) {
			hexToHighlight = hightlightedHexes.get(0);
		} else {
			for (int j = i + 1; j < hightlightedHexes.size(); j++) {
				if (hightlightedHexes.get(j) != currentSelectedHex) {
					hexToHighlight = hightlightedHexes.get(j);
					break;
				}
			}
			if (hexToHighlight == null) {
				hexToHighlight = hightlightedHexes.get(0);
			}
		}
		Point selectedHex = new Point(hexToHighlight / 100, hexToHighlight / 100);
		Application.instance().getApplicationContext().publishEvent(new JOverseerEvent(LifecycleEventsEnum.SelectedHexChangedEvent.toString(), selectedHex, this));

	}

}
