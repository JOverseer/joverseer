package org.joverseer.ui.command;

import org.joverseer.ui.support.GraphicUtils;
import org.springframework.richclient.command.ActionCommand;


public class ShowTipOfTheDayCommand extends ActionCommand {
    
    public ShowTipOfTheDayCommand() {
        super("showTipOfTheDayCommand");
    }

    protected void doExecuteCommand() {
        GraphicUtils.showTipOfTheDay();
    }

}
