package org.joverseer.ui.command;

import org.joverseer.JOApplication;
import org.joverseer.ui.LifecycleEventsEnum;
import org.springframework.richclient.command.ActionCommand;

public class PalantirStyleMapCommand extends ActionCommand {
    public PalantirStyleMapCommand() {
        super("palantirStyleMapCommand");
    }

    @Override
	protected void doExecuteCommand() {
    	JOApplication.publishEvent(LifecycleEventsEnum.SetPalantirMapStyleEvent, this, this);
    }
}
