package org.joverseer.ui.views;

import org.joverseer.tools.GameEncounterReportCollector;

public class GameEncounterReportView extends BaseHtmlReportView {

	@Override
	protected String getReportContents() {
		GameEncounterReportCollector gerc = new GameEncounterReportCollector();
		return gerc.CollectEncounters();
	}

	@Override
	protected void handleHyperlinkEvent(String url) {
	}

}
