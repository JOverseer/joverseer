package org.joverseer.ui.listviews.commands;

import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.richclient.application.Application;
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
		MessageSource ms = (MessageSource) Application.services().getService(MessageSource.class);
		MessageDialog dlg = new MessageDialog(ms.getMessage("listViewDescriptionDialog.title", new Object[] {}, Locale.getDefault()), ms.getMessage(listViewName + ".description", new Object[] {}, Locale.getDefault()));
		dlg.showDialog();
	}
}
