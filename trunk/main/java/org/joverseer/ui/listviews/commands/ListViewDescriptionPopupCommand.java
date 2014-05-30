package org.joverseer.ui.listviews.commands;

import org.joverseer.ui.support.Messages;
import org.springframework.richclient.command.ActionCommand;
import org.springframework.richclient.dialog.MessageDialog;

public class ListViewDescriptionPopupCommand extends ActionCommand {
	String listViewName;

	public ListViewDescriptionPopupCommand(String listViewName) {
		super("listViewDescriptionPopupCommand");
		this.listViewName = listViewName;
	}

	@Override
	protected void doExecuteCommand() {
		MessageDialog dlg = new MessageDialog(Messages.getString("listViewDescriptionDialog.title"),
				Messages.getString(this.listViewName + ".description"));
		dlg.showDialog();
	}
}
