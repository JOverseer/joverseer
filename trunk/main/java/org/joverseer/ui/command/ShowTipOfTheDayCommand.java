package org.joverseer.ui.command;

import org.joverseer.ui.support.GraphicUtils;
import org.springframework.richclient.command.ActionCommand;

/**
 * Shows the tip of the day form
 * 
 * @author Marios Skounakis
 */
public class ShowTipOfTheDayCommand extends ActionCommand {
    
    public ShowTipOfTheDayCommand() {
        super("showTipOfTheDayCommand");
    }

    @Override
	protected void doExecuteCommand() {
        GraphicUtils.showTipOfTheDay();
    }

}
