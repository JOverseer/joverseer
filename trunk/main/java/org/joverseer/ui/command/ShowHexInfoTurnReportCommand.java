package org.joverseer.ui.command;

import org.joverseer.ui.LifecycleEventsEnum;
import org.joverseer.ui.domain.mapItems.AbstractMapItem;
import org.joverseer.ui.domain.mapItems.HexInfoTurnReportMapItem;
import org.joverseer.ui.support.ActiveGameChecker;
import org.joverseer.ui.support.JOverseerEvent;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.command.ActionCommand;

/**
 * Generates a Hex Info Turn Report (i.e. shows the turn each hex was last reconed on the map)
 * @author Marios Skounakis
 *
 */
public class ShowHexInfoTurnReportCommand extends ActionCommand {
    public ShowHexInfoTurnReportCommand() {
        super("showHexInfoTurnReportCommand");
    }
    
    @Override
	protected void doExecuteCommand() {
        if (!ActiveGameChecker.checkActiveGameExists()) return;

        HexInfoTurnReportMapItem hitrmi = new HexInfoTurnReportMapItem();
        AbstractMapItem.add(hitrmi);
        Application.instance().getApplicationContext().publishEvent(
                new JOverseerEvent(LifecycleEventsEnum.RefreshMapItems.toString(), hitrmi, this));
    }
}
