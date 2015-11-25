package org.joverseer.ui.command;

import java.awt.Desktop;
import java.net.URI;

import org.springframework.richclient.command.ActionCommand;

public class ShowMapHelpCommand extends ActionCommand {
	public ShowMapHelpCommand() {
		super("ShowMapHelpCommand");
	}

	@Override
	protected void doExecuteCommand() {
		try {
			Desktop.getDesktop().browse(new URI("https://github.com/JOverseer/joverseer/wiki/MapInfo"));
		} catch (Exception e) {
		}
		;
	}
}
