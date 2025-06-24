package com.jidesoft.spring.richclient;

import org.springframework.richclient.application.Application;
import org.springframework.richclient.application.ApplicationWindow;
import org.springframework.richclient.command.ActionCommand;

import com.jidesoft.spring.richclient.docking.JideApplicationWindow;

/**
 * A command that closes the active window and saves the window layout.
 * Use this instead of the default exit command in order to save the window layout 
 * when exiting the application
 * 
 * @author Marios Skounakis
 *
 */
public class ExitCommand extends ActionCommand {
    
    public ExitCommand() {
        super("exitCommand");
    }

    @Override
	protected void doExecuteCommand() {
    	ApplicationWindow window =Application.instance().getActiveWindow();
    	if (JideApplicationWindow.class.isInstance(window)) {
    		((JideApplicationWindow)window).saveLayoutData(null);
    	}
    	window.close();
    }
}
