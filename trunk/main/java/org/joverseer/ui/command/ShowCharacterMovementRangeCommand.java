package org.joverseer.ui.command;

import org.joverseer.ui.LifecycleEventsEnum;
import org.joverseer.ui.domain.mapItems.AbstractMapItem;
import org.joverseer.ui.domain.mapItems.CharacterRangeMapItem;
import org.joverseer.ui.map.MapPanel;
import org.joverseer.ui.support.JOverseerEvent;
import org.springframework.richclient.application.Application;
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
        AbstractMapItem.add(crmi);
        Application.instance().getApplicationContext().publishEvent(
                new JOverseerEvent(LifecycleEventsEnum.RefreshMapItems.toString(), MapPanel.instance()
                        .getSelectedHex(), this));
    }
}
