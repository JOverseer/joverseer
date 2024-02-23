package org.joverseer.ui.command;

import java.awt.Desktop;
import java.net.URI;

import org.springframework.richclient.command.ActionCommand;

public class ShowMapHelpCommand extends ActionCommand {
	String link;
	public String getLink() {
		return this.link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public ShowMapHelpCommand() {
		super("ShowMapHelpCommand");
	}

	@Override
	protected void doExecuteCommand() {
		try {
			Desktop.getDesktop().browse(new URI(this.link));
		} catch (Exception e) {
		}
		;
	}
}
