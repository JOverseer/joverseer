package org.joverseer.ui.views;

import org.joverseer.game.Game;
import org.joverseer.support.GameHolder;
import org.joverseer.tools.turnReport.BaseReportObject;
import org.joverseer.tools.turnReport.TurnReportCollector;

public class TurnReportView extends BaseHtmlReportView {
	public TurnReportView() {
		super(true);
	}
	@Override
	protected String getReportContents() {
		super.getReportContents(); // flag that we've called.
		Game g = this.gameHolder.getGame();
		if (!GameHolder.hasInitializedGame())
			return "";
		TurnReportCollector trc = new TurnReportCollector(this.gameHolder);
		return trc.renderReport(g);
	}

	@Override
	protected void handleHyperlinkEvent(String url) {

		BaseReportObject.processHyperlink(url,GameHolder.getTurnOrNull(this.gameHolder),this.gameHolder.getGame());
	}

}
