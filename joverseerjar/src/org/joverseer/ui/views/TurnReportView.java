package org.joverseer.ui.views;

import org.joverseer.tools.turnReport.BaseReportObject;
import org.joverseer.tools.turnReport.TurnReportCollector;

public class TurnReportView extends BaseHtmlReportView {
	public TurnReportView() {
		super(true);
	}
	@Override
	protected String getReportContents() {
		super.getReportContents(); // flag that we've called.
		TurnReportCollector trc = new TurnReportCollector();
		return trc.renderReport();
	}

	@Override
	protected void handleHyperlinkEvent(String url) {
		BaseReportObject.processHyperlink(url);
	}

}
