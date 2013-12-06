package org.joverseer.ui.command;

import org.joverseer.ui.LifecycleEventsEnum;
import org.joverseer.ui.support.JOverseerEvent;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.command.ActionCommand;

public class PalantirStyleMapCommand extends ActionCommand {
    public PalantirStyleMapCommand() {
        super("palantirStyleMapCommand");
    }

    @Override
	protected void doExecuteCommand() {
    	Application.instance().getApplicationContext().publishEvent(
                new JOverseerEvent(LifecycleEventsEnum.SetPalantirMapStyleEvent.toString(), this, this));
    }
}
