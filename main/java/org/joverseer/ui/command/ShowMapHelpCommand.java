package org.joverseer.ui.command;

import java.awt.Desktop;
import java.net.URI;
import java.util.Locale;

import org.joverseer.ui.views.CreditsForm;
import org.springframework.binding.form.FormModel;
import org.springframework.context.MessageSource;
import org.springframework.core.io.Resource;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.command.AbstractCommand;
import org.springframework.richclient.command.ActionCommand;
import org.springframework.richclient.dialog.FormBackedDialogPage;
import org.springframework.richclient.dialog.TitledPageApplicationDialog;
import org.springframework.richclient.form.FormModelHelper;

public class ShowMapHelpCommand extends ActionCommand {
    public ShowMapHelpCommand() {
        super("ShowMapHelpCommand");
    }

    protected void doExecuteCommand() {
    	try {
    		Desktop.getDesktop().browse(new URI("http://code.google.com/p/joverseer/wiki/MapInfo"));
    	}
    	catch (Exception e) {
    		int a = 1;
    	};
    }
}
