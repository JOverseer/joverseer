package org.joverseer.ui.command;

import java.awt.Desktop;
import java.net.URI;

import org.springframework.richclient.command.ActionCommand;

public class ShowLinkCommand extends ActionCommand {

	String link;
	String id;
	public String getLink() {
		return this.link;
	}

	public void setLink(String link) {
		this.link = link;
	}
	
	public String getid() {
		return this.id;
	}

	public void setid(String id) {
		this.id = id;
		this.setBeanName(this.id);
	}

	public ShowLinkCommand() {
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
