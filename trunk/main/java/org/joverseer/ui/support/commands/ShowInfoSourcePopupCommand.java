package org.joverseer.ui.support.commands;

import org.joverseer.support.infoSources.InfoSource;
import org.springframework.richclient.command.ActionCommand;
import org.springframework.richclient.dialog.MessageDialog;

public class ShowInfoSourcePopupCommand extends ActionCommand {
	InfoSource infoSource;

	public ShowInfoSourcePopupCommand(InfoSource infoSource) {
		super();
		this.infoSource = infoSource;
	}

	@Override
	protected void doExecuteCommand() {
		if (this.infoSource != null) {
			MessageDialog dlg = new MessageDialog("Info Source", this.infoSource
					.getDescription());
			dlg.showDialog();
		}
	}

}
