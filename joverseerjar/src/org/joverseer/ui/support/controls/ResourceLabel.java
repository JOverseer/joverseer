package org.joverseer.ui.support.controls;

import javax.swing.JLabel;

import org.springframework.richclient.application.Application;

public class ResourceLabel extends JLabel {

	private static final long serialVersionUID = 1L;

	public ResourceLabel(String text) {
		super(Application.instance().getApplicationContext().getMessage(text, null, null));
	}

}
