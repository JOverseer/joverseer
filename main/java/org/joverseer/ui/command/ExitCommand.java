package org.joverseer.ui.command;

import org.springframework.richclient.application.Application;
import org.springframework.richclient.application.ApplicationWindow;
import org.springframework.richclient.command.ActionCommand;

public class ExitCommand extends ActionCommand {
    
    public ExitCommand() {
        super("exitCommand");
    }

    protected void doExecuteCommand() {
    	Application.instance().getActiveWindow().close();
    }
}
