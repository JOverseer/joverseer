package org.joverseer.ui.command;

import org.joverseer.support.Container;
import org.joverseer.ui.LifecycleEventsEnum;
import org.joverseer.ui.support.JOverseerEvent;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.command.ActionCommand;


public class ClearMapItems  extends ActionCommand {
    
    public ClearMapItems() {
        super("clearMapItemsCommand");
    }

    protected void doExecuteCommand() {
        Container mapItems = (Container)Application.instance().getApplicationContext().getBean("mapItemContainer");
        mapItems.removeAll(mapItems.getItems());
        Application.instance().getApplicationContext().publishEvent(
                new JOverseerEvent(LifecycleEventsEnum.RefreshMapItems.toString(), this, this));

    }
}
