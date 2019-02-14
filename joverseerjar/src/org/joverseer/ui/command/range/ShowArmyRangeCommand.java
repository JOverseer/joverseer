/**
 * 
 */
package org.joverseer.ui.command.range;

import org.joverseer.JOApplication;
import org.joverseer.ui.LifecycleEventsEnum;
import org.joverseer.ui.domain.mapItems.AbstractMapItem;
import org.joverseer.ui.domain.mapItems.ArmyRangeMapItem;
import org.joverseer.ui.map.MapPanel;
import org.springframework.richclient.command.ActionCommand;

public class ShowArmyRangeCommand extends ActionCommand {
	boolean cav;
	boolean fed;
	int hexNo;

	public ShowArmyRangeCommand(String arg0, boolean cav, boolean fed, int hexNo) {
		super(arg0);
		this.cav = cav;
		this.fed = fed;
		this.hexNo = hexNo;
	}

	@Override
	protected void doExecuteCommand() {
		ArmyRangeMapItem armi = new ArmyRangeMapItem(this.hexNo, this.cav, this.fed);
		AbstractMapItem.toggle(armi);

		JOApplication.publishEvent(LifecycleEventsEnum.RefreshMapItems, MapPanel.instance().getSelectedHex(), this);
	}
}