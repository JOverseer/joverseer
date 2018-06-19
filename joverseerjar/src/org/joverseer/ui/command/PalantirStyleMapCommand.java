package org.joverseer.ui.command;

import org.joverseer.joApplication;
import org.joverseer.ui.LifecycleEventsEnum;
import org.springframework.richclient.command.ActionCommand;

public class PalantirStyleMapCommand extends ActionCommand {
    public PalantirStyleMapCommand() {
        super("palantirStyleMapCommand");
    }

    @Override
	protected void doExecuteCommand() {
    	joApplication.publishEvent(LifecycleEventsEnum.SetPalantirMapStyleEvent, this, this);
    }
}
