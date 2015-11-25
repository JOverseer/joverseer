package org.joverseer.ui.support.controls;

import javax.swing.JButton;

import org.springframework.richclient.application.Application;

public class ResourceButton extends JButton {

	public ResourceButton(String text) {
		super(Application.instance().getApplicationContext().getMessage(text, null, null));
	}

}
