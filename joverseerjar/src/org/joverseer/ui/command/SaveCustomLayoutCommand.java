package org.joverseer.ui.command;

import java.util.Locale;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.MessageSource;
import org.springframework.core.io.Resource;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.command.support.ApplicationWindowAwareCommand;
import org.springframework.richclient.dialog.ConfirmationDialog;
import org.springframework.richclient.dialog.MessageDialog;

import com.jidesoft.docking.DockingManager;
import com.jidesoft.spring.richclient.docking.JideApplicationWindow;

public class SaveCustomLayoutCommand extends ApplicationWindowAwareCommand {
	private static final Log log = LogFactory.getLog(SaveCustomLayoutCommand.class);

	private static final String ID = "saveCustomLayoutCommand";

	public SaveCustomLayoutCommand() {
		super(ID);
	}

	@Override
	protected void doExecuteCommand() {
		log.debug("Execute command");
		
		JideApplicationWindow AppWindow = ((JideApplicationWindow) getApplicationWindow());
		//Resource r = Application.instance().getApplicationContext().getResource("classpath:layout/ClassicDefault.layout");
		try {
			AppWindow.saveLayoutData("Custom");
        } catch (Exception exc) {
			log.error("Failed to save layout " + exc.getMessage());
		}
		
		MessageSource ms = (MessageSource) Application.services().getService(MessageSource.class);
		MessageDialog md = new MessageDialog(ms.getMessage("saveCustomLayoutCommand.title", new String[] {}, Locale.getDefault()), ms.getMessage("saveCustomLayoutCommand.message", new String[] {}, Locale.getDefault()));
		md.showDialog();

	}
}
