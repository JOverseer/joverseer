package org.joverseer.ui.command;

import org.joverseer.JOApplication;
import org.joverseer.ui.LifecycleEventsEnum;
import org.joverseer.ui.domain.mapItems.AbstractMapItem;
import org.joverseer.ui.domain.mapItems.HexInfoTurnReportMapItem;
import org.joverseer.ui.support.ActiveGameChecker;
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
        JOApplication.publishEvent(LifecycleEventsEnum.RefreshMapItems, hitrmi, this);
    }
}
