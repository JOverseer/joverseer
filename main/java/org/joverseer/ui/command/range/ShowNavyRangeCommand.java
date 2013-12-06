/**
 * 
 */
package org.joverseer.ui.command.range;

import org.joverseer.ui.LifecycleEventsEnum;
import org.joverseer.ui.domain.mapItems.AbstractMapItem;
import org.joverseer.ui.domain.mapItems.NavyRangeMapItem;
import org.joverseer.ui.map.MapPanel;
import org.joverseer.ui.support.JOverseerEvent;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.command.ActionCommand;

public class ShowNavyRangeCommand extends ActionCommand {
	boolean openSeas;
	boolean fed;
	int hexNo;

	public ShowNavyRangeCommand(String arg0, boolean fed, boolean openSeas, int hexNo) {
		super(arg0);
		this.openSeas = openSeas;
		this.fed = fed;
		this.hexNo = hexNo;
	}

	@Override
	protected void doExecuteCommand() {
		NavyRangeMapItem armi = new NavyRangeMapItem(this.hexNo, this.fed, this.openSeas);
		AbstractMapItem.add(armi);

		Application.instance().getApplicationContext().publishEvent(new JOverseerEvent(LifecycleEventsEnum.RefreshMapItems.toString(), MapPanel.instance().getSelectedHex(), this));
	}
}