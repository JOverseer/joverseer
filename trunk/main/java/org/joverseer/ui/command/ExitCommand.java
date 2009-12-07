package org.joverseer.ui.command;

import org.springframework.richclient.application.Application;
import org.springframework.richclient.application.ApplicationWindow;
import org.springframework.richclient.command.ActionCommand;

import com.jidesoft.spring.richclient.docking.JideApplicationPage;
import com.jidesoft.spring.richclient.docking.JideApplicationWindow;
import com.jidesoft.spring.richclient.docking.LayoutManager;
import com.jidesoft.spring.richclient.perspective.Perspective;

public class ExitCommand extends ActionCommand {
    
    public ExitCommand() {
        super("exitCommand");
    }

    protected void doExecuteCommand() {
    	ApplicationWindow window =Application.instance().getActiveWindow();
    	if (JideApplicationWindow.class.isInstance(window)) {
    		((JideApplicationWindow)window).saveLayoutData();
    	}
    	window.close();
    }
}
