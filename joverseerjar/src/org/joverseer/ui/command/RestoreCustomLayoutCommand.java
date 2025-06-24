package org.joverseer.ui.command;

import java.util.Locale;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.MessageSource;
import org.springframework.core.io.Resource;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.command.support.ApplicationWindowAwareCommand;
import org.springframework.richclient.dialog.ConfirmationDialog;

import com.jidesoft.docking.DockingManager;
import com.jidesoft.spring.richclient.docking.JideApplicationPage;
import com.jidesoft.spring.richclient.docking.JideApplicationWindow;
import com.jidesoft.spring.richclient.docking.LayoutManager;
import com.jidesoft.spring.richclient.perspective.Perspective;

public class RestoreCustomLayoutCommand extends ApplicationWindowAwareCommand {
	private static final Log log = LogFactory.getLog(RestoreCustomLayoutCommand.class);

	private static final String ID = "restoreCustomLayoutCommand";

	public RestoreCustomLayoutCommand() {
		super(ID);
	}

	@Override
	protected void doExecuteCommand() {
		log.debug("Execute command");
		MessageSource ms = (MessageSource) Application.services().getService(MessageSource.class);
		ConfirmationDialog md = new ConfirmationDialog(ms.getMessage("restoreCustomLayoutCommand.title", new String[] {}, Locale.getDefault()), ms.getMessage("confirmRestoreLayoutDialog.message", new String[] {}, Locale.getDefault())) {

			@Override
			protected void onConfirm() {
				JideApplicationWindow appWindow = ((JideApplicationWindow) getApplicationWindow());
				DockingManager manager = appWindow.getDockingManager();
				LayoutManager.loadPageLayoutData(manager, ((JideApplicationPage)appWindow.getPage()).getId(), ((JideApplicationPage)appWindow.getPage()).getPerspectiveManager().getCurrentPerspective(), "Custom");

			}

		};
		md.showDialog();

	}
}
