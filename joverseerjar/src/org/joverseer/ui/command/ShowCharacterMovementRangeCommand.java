package org.joverseer.ui.command;

import org.joverseer.joApplication;
import org.joverseer.ui.LifecycleEventsEnum;
import org.joverseer.ui.domain.mapItems.AbstractMapItem;
import org.joverseer.ui.domain.mapItems.CharacterRangeMapItem;
import org.joverseer.ui.map.MapPanel;
import org.springframework.richclient.command.ActionCommand;

/**
 * Shows the movement range of a character on the map
 * 
 * @author Marios Skounakis
 */
public class ShowCharacterMovementRangeCommand extends ActionCommand {
	int hexNo;
	int range;
	
	public ShowCharacterMovementRangeCommand(int hexNo, int range) {
		super();
		this.hexNo = hexNo;
		this.range = range;
	}
	
	@Override
	protected void doExecuteCommand() {
        CharacterRangeMapItem crmi = new CharacterRangeMapItem(this.hexNo, this.range);
        AbstractMapItem.toggle(crmi);
        joApplication.publishEvent(LifecycleEventsEnum.RefreshMapItems, MapPanel.instance().getSelectedHex(), this);
    }
}
